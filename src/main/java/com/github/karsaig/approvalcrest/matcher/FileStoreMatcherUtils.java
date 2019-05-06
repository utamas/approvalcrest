package com.github.karsaig.approvalcrest.matcher;

import com.github.karsaig.approvalcrest.ApprovedFile;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Utility class with methods for creating the JSON files for
 * {@link JsonMatcher}.
 *
 * @author Andras_Gyuro
 *
 */
public class FileStoreMatcherUtils {
    private static String hashOf(final String fileName, int length) {
        return Hashing.sha1().hashString(fileName, Charsets.UTF_8).toString().substring(0, length);
    }

    public static class ApprovedFileMeta {

        private final String testMethodName;
        private final String testClassName;
        private final String fileName;
        private final String filePath;

        public ApprovedFileMeta(String testMethodName, String testClassName, String fileName, String filePath) {
            this.testMethodName = testMethodName;
            this.testClassName = testClassName;
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String getTestMethodName() {
            return testMethodName;
        }

        public String getTestClassName() {
            return testClassName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }
    }

	public static final Object SEPARATOR = "-";
	private static final String SRC_TEST_JAVA_PATH = "src" + File.separator + "test" + File.separator + "java"
			+ File.separator;
	private static final String APPROVED_NAME_PART = "approved";
	private static final String NOT_APPROVED_NAME_PART = "not-approved";
	private final String fileExtension;

	public FileStoreMatcherUtils(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Creates file with '-not-approved' suffix and .json extension and writes
	 * the jsonObject in it.
	 *
	 * @param fileNameWithPath
	 *            specifies the name of the file with full path (relative to
	 *            project root)
	 * @param jsonObject
	 *            the file's content
	 * @throws IOException
	 *             exception thrown when failed to create the file
	 */
	public String createNotApproved(final String fileNameWithPath, final String jsonObject, final String comment)
			throws IOException {
		File file = new File(getFullFileName(fileNameWithPath, false));
		File parent = file.getParentFile();
		parent.mkdirs();
		parent.setExecutable(true, false);
		parent.setReadable(true, false);
		parent.setWritable(true, false);
		return writeToFile(file, jsonObject, comment);
	}

	public String overwriteApprovedFile(String fileNameWithPath, final String jsonObject, final String comment) throws IOException {
		File file = new File(getFullFileName(fileNameWithPath, true));
		return writeToFile(file, jsonObject, comment);
	}

	private String writeToFile(File file,String jsonObject, String comment) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = Files.newWriter(file, Charsets.UTF_8);
			writer.append("/*" + comment + "*/");
			writer.append("\n");
			writer.append(jsonObject);
			writer.close();
			file.setReadable(true, false);
			file.setWritable(true, false);
			return file.getName();
		}
		finally {
			if(writer != null) {
				writer.close();
			}
		}
	}

	public String readFile(File file) throws IOException {
		String fileContent = Files.toString(file, Charsets.UTF_8);

		if (fileContent.startsWith("/*")) {
			int index = fileContent.indexOf("*/\n");
			if (-1 < index) {
				return fileContent.substring(index + 3);
			}
		}
		return fileContent;
	}

	/**
	 * Gets file with '-approved' suffix and .json extension and returns it.
	 *
	 * @param fileNameWithPath
	 *            the name of the file with full path (relative to project root)
	 * @return the {@link File} object
	 */
	public File getApproved(final String fileNameWithPath) {
		File file = new File(getFullFileName(fileNameWithPath, true));
		return file;
	}

    private static class ClassCache {
        private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

        Class<?> get(String className) {
            try {
                if (!classes.containsKey(className)) {
                    classes.put(className, Class.forName(className));
                }

                return classes.get(className);
            } catch (ClassNotFoundException cause) {
                throw new RuntimeException(cause);
            }
        }
    }

    public ApprovedFileMeta getTestCaseMeta(int length) {
        ApprovedFileMeta approvedFileMeta = null;

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        ClassCache cache = new ClassCache();

        int index = 0;
        boolean isTestMethod = false;
        while (index < trace.length && !isTestMethod) {
            StackTraceElement element = trace[index];

            String className = element.getClassName();
            String methodName = element.getMethodName();

            Class<?> type = cache.get(className);

            Method method = findMethod(type, methodName);

            isTestMethod = method != null && method.isAnnotationPresent(Test.class);

            if (isTestMethod) {
                String fileName = hashOf(methodName, length);
                String path = hashOf(className, length);

                ApprovedFile approvedFile = method.getAnnotation(ApprovedFile.class);
                if (approvedFile != null) {
                    fileName = nonBlankValueOrDefaultTo(approvedFile.name(), fileName);
                    path = nonBlankValueOrDefaultTo(approvedFile.path(), path);
                }
                approvedFileMeta = new ApprovedFileMeta(methodName, className, fileName, path);
            }

            index++;
        }

        checkState(isTestMethod, "Non of the method is the call stack is annotated with %s", Test.class.getCanonicalName());

        return approvedFileMeta;
    }

	/**
	 * Returns the absolute path of the test class in which the call was
	 * originated from.
	 *
	 * @return test method name in String
	 */
	public String getCallerTestClassPath() {
		StackTraceElement testStackTraceElement = getTestStackTraceElement(Thread.currentThread().getStackTrace());
		String fileName = testStackTraceElement.getFileName().substring(0,
				testStackTraceElement.getFileName().lastIndexOf("."));
		return SRC_TEST_JAVA_PATH
				+ testStackTraceElement.getClassName().replace(".", File.separator).replace(fileName, "");
	}

	private StackTraceElement getTestStackTraceElement(final StackTraceElement[] stackTrace) {
		StackTraceElement result = null;
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement s = stackTrace[i];
			if (isTestMethod(s)) {
				result = s;
				break;
			}
		}
		return result;
	}

	private boolean isTestMethod(final StackTraceElement element) {
		boolean isTest;

		String fullClassName = element.getClassName();
		Class<?> clazz;
		try {
			clazz = Class.forName(fullClassName);
			Method method = findMethod(clazz, element.getMethodName());
			isTest = method != null && method.isAnnotationPresent(Test.class);
		} catch (Throwable e) {
			isTest = false;
		}

		return isTest;
	}

	private Method findMethod(Class clazz, String methodName) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	public String getFullFileName(final String fileName, final boolean approved) {
		return getFileNameWithExtension(fileName, approved);
	}

	private String getFileNameWithExtension(final String fileName, final boolean approved) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(fileName);
		stringBuilder.append(SEPARATOR);
		if (approved) {
			stringBuilder.append(APPROVED_NAME_PART);
		} else {
			stringBuilder.append(NOT_APPROVED_NAME_PART);
		}
		stringBuilder.append(fileExtension);

		return stringBuilder.toString();
	}

    private String nonBlankValueOrDefaultTo(String fileName, String methodName) {
        return fileName == null ? methodName : fileName.trim().isEmpty() ? methodName : fileName;
    }
}
