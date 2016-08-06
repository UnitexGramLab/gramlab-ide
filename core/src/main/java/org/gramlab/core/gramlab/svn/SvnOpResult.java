package org.gramlab.core.gramlab.svn;

public enum SvnOpResult {
	OK,
	AUTHENTICATION_REQUIRED,
	COMMIT_FORBIDDEN,
	COMMIT_FAILED,
	OUT_OF_DATE,
	UNKNOWN_ERROR, 
	NOT_A_WORKING_COPY
}
