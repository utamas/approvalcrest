package com.github.karsaig.approvalcrest.matcher;

import com.github.karsaig.approvalcrest.ComparisonDescription;
import com.github.karsaig.approvalcrest.matcher.FileStoreMatcherUtils.ApprovedFileMeta;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;

import java.io.File;
import java.io.IOException;

import static com.github.karsaig.approvalcrest.matcher.FileStoreMatcherUtils.SEPARATOR;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.fail;

/**
 * <p>
 * Matcher for asserting expected {@link String}s. Searches for an approved file
 * in the same directory as the test file:
 * <ul>
 * <li>If found, the matcher will assert the contents of the file to the actual
 * {@link String}.</li>
 * <li>If not found, a non-approved file is created, that must be verified and
 * renamed to "*-approved.content" by the developer.</li>
 * </ul>
 * The files and directories are hashed with SHA-1 algorithm by default to avoid
 * too long file and path names. These are generated in the following way:
 * <ul>
 * <li>the directory name is the first {@value #NUM_OF_HASH_CHARS} characters of
 * the hashed <b>class name</b>.</li>
 * <li>the file name is the first {@value #NUM_OF_HASH_CHARS} characters of the
 * hashed <b>test method name</b>.</li>
 * </ul>
 *
 * This default behavior can be overridden by using the
 * {@link #withFileName(String)} for custom file name and
 * {@link #withPathName(String)} for custom path.
 * </p>
 * 
 * @param <T>
 *            Only {@link String} is supported at the moment.
 */
public class ContentMatcher<T> extends DiagnosingMatcher<T> implements ApprovedFileMatcher<ContentMatcher<T>> {

	private static final int NUM_OF_HASH_CHARS = 6;
	private static final String UPDATE_IN_PLACE_NAME = "jsonMatcherUpdateInPlace";

	private String pathName;
	private String customPathName;
	private String fileName;
	private String customFileName;
	private String fileNameWithPath;
	private String uniqueId;
	private String testClassName;
	private String testMethodName;
	private String testClassNameHash;

	private FileStoreMatcherUtils fileStoreMatcherUtils = new FileStoreMatcherUtils(".content");

	private String expectedContent;

	@Override
	public void describeTo(Description description) {
		description.appendText(expectedContent);
	}

	@Override
	protected boolean matches(Object actual, Description mismatchDescription) {
		boolean matches = false;
		init();
		createNotApprovedFileIfNotExists(actual);
		initExpectedFromFile();
		String actualString = String.class.cast(actual);

		if (expectedContent.equals(actualString)) {
			matches = true;
		} else {
			if ("true".equals(System.getProperty(UPDATE_IN_PLACE_NAME))) {
				overwriteApprovedFile(actual);
				matches = true;
			} else {
				matches = appendMismatchDescription(mismatchDescription, expectedContent, actualString,
						getAssertMessage("Content does not match!"));
			}
		}
		return matches;
	}

	@Override
	public ContentMatcher<T> withUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
		return this;
	}

	@Override
	public ContentMatcher<T> withFileName(String customFileName) {
		this.customFileName = customFileName;
		return this;
	}

	@Override
	public ContentMatcher<T> withPathName(String pathName) {
		this.customPathName = pathName;
		return this;
	}

	private void init() {
		ApprovedFileMeta meta = fileStoreMatcherUtils.getTestCaseMeta(NUM_OF_HASH_CHARS);

		testMethodName = meta.getTestMethodName();
		testClassName = meta.getTestClassName();

		fileName = isBlank(customFileName) ? meta.getFileName() : customFileName;

		if (uniqueId != null) {
			fileName += SEPARATOR + uniqueId;
		}

		if (isBlank(customPathName)) {
			testClassNameHash = meta.getFilePath();
			pathName = fileStoreMatcherUtils.getCallerTestClassPath() + File.separator + testClassNameHash;
		} else {
			pathName = customPathName;
		}

		fileNameWithPath = pathName + File.separator + fileName;
	}

	private void createNotApprovedFileIfNotExists(final Object toApprove) {
		File approvedFile = fileStoreMatcherUtils.getApproved(fileNameWithPath);

		if (!approvedFile.exists()) {
			try {
				String approvedFileName = approvedFile.getName();
				if (!String.class.isInstance(toApprove)) {
					throw new IllegalArgumentException("Only String content matcher is supported!");
				}
				String content = String.class.cast(toApprove);
				String createdFileName = fileStoreMatcherUtils.createNotApproved(fileNameWithPath, content,
						getCommentLine());
				String message;
				if (testClassNameHash == null) {
					message = "Not approved file created: '" + createdFileName
							+ "';\n please verify its contents and rename it to '" + approvedFileName + "'.";
				} else {
					message = "Not approved file created: '" + testClassNameHash + File.separator + createdFileName
							+ "';\n please verify its contents and rename it to '" + approvedFileName + "'.";
				}
				fail(message);

			} catch (IOException e) {
				throw new IllegalStateException(
						String.format("Exception while creating not approved file %s", toApprove.toString()), e);
			}
		}
	}

	private void overwriteApprovedFile(Object actual) {
		File approvedFile = fileStoreMatcherUtils.getApproved(fileNameWithPath);
		if (approvedFile.exists()) {
			try {
				String content = String.class.cast(actual);
				fileStoreMatcherUtils.overwriteApprovedFile(fileNameWithPath, content, getCommentLine());
			} catch (IOException e) {
				throw new IllegalStateException(
						String.format("Exception while overwriting approved file %s", actual.toString()), e);
			}
		} else {
			throw new IllegalStateException(
					"Approved file " + fileNameWithPath + " must exist in order to overwrite it! ");
		}
	}

	private String getCommentLine() {
		return testClassName + "." + testMethodName;
	}

	private void initExpectedFromFile() {
		File approvedFile = fileStoreMatcherUtils.getApproved(fileNameWithPath);
		try {
			expectedContent = fileStoreMatcherUtils.readFile(approvedFile);
		} catch (IOException e) {
			throw new IllegalStateException(
					String.format("Exception while initializing expected from file: %s", approvedFile.toString()), e);
		}
	}

	private boolean appendMismatchDescription(final Description mismatchDescription, final String expected,
			final String actual, final String message) {
		if (mismatchDescription instanceof ComparisonDescription) {
			ComparisonDescription shazamMismatchDescription = (ComparisonDescription) mismatchDescription;
			shazamMismatchDescription.setComparisonFailure(true);
			shazamMismatchDescription.setExpected(expected);
			shazamMismatchDescription.setActual(actual);
			shazamMismatchDescription.setDifferencesMessage(message);
		}
		mismatchDescription.appendText(message);
		return false;
	}

	private String getAssertMessage(final String message) {
		String result;
		if (testClassNameHash == null) {
			result = "Expected file " + fileNameWithPath + "\n" + message;
		} else {
			result = "Expected file " + testClassNameHash + File.separator
					+ fileStoreMatcherUtils.getFullFileName(fileName, true) + "\n" + message;
		}
		return result;
	}
}
