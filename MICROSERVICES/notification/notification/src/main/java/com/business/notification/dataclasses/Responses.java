package com.business.notification.dataclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Responses {

	SUCCESS(Constants.SUCCESS_CODE, Constants.SUCCESS_MSG),
	PARTIAL_SUCCESS(Constants.GENERAL_PARTIAL_SUCCESS_CODE, Constants.GENERAL_PARTIAL_SUCCESS_CODE),

	GENERAL_SYSTEM_ERROR(Constants.SYSTEM_ERROR_CODE, Constants.SYSTEM_ERROR_MSG),

	GENERAL_REQUEST_TIMEOUT(Constants.SYSTEM_ERROR_CODE, Constants.SYSTEM_ERROR_MSG),

	GENERAL_INVALID_INPUT(Constants.GENERAL_INVALID_INPUT_CODE, Constants.GENERAL_INVALID_INPUT_MSG),

	GENERAL_DATA_NOT_FOUND_ERROR(Constants.GENERAL_NOT_FOUND_CODE, Constants.GENERAL_NOT_FOUND_MSG),

	GENERAL_INTERNAL_PROCESSING_ERROR(Constants.GENERAL_INTERNAL_PROCESSING_ERROR_CODE,
			Constants.GENERAL_INTERNAL_PROCESSING_ERROR_MSG),

	GENERAL_INVALID_INPUT_BLANK_OBJECT(Constants.GENERAL_INVALID_INPUT_CODE, Constants.GENERAL_INVALID_BLANK_REQUEST),

	GENERAL_INVALID_REQUEST_OBJECT_ERROR(Constants.GENERAL_INVALID_REQUEST_OBJECT_CODE,
			Constants.GENERAL_INVALID_REQUEST_OBJECT_MSG),

	GENERAL_SERVICE_NOT_AVAILABLE_ERROR(Constants.GENERAL_SERVICE_NOT_AVAILABLE_CODE,
			Constants.GENERAL_SERVICE_NOT_AVAILABLE_MSG),

	GENERAL_DUPLICATE_REQUEST_OBJECT_ERROR(Constants.DUPLICATE_REQUEST_CODE, Constants.DUPLICATE_REQUEST_MSG),

	INVALID_LOGIN_CREDENTIALS(Constants.GENERAL_INVALID_LOGIN_CREDENTIALS_CODE, Constants.BAD_CREDENTIALS),

	INSUFFICIENT_PERMISSION(Constants.GENERAL_INVALID_LOGIN_CREDENTIALS_CODE, Constants.INSUFFICIENT_PERMISSION),

	INVALID_UPLOAD_TOKEN(Constants.INVALID_TOKEN_CODE, Constants.INVALID_UPLOAD_TOKEN),

	EXPIRED_UPLOAD_TOKEN(Constants.EXPIRED_TOKEN_CODE, Constants.EXPIRED_UPLOAD_TOKEN),

	INVALID_OWNERSHIP_SHARE_ERROR(Constants.GENERAL_INVALID_INPUT_CODE, Constants.INVALID_SHARE),

	ACCOUNT_LOCKED(Constants.ACCOUNT_LOCKED_CODE, Constants.ACCOUNT_LOCKED_MSG),

	ADDITIONAL_DATA_REQUIRED(Constants.ADDITIONAL_DATA_REQUIRED, Constants.ADDITIONAL_DATA_REQUIRED_MSG),

	INVALID_AUTH_TOKEN(Constants.INVALID_AUTH_TOKEN_CODE, Constants.INVALID_AUTH_TOKEN_MSG),
	ACCESS_DENIED(Constants.ACCESS_DENIED_CODE, Constants.ACCESS_DENIED_MSG),
	
	GENERAL_INTERNET_EXCEPTION(Constants.GENERAL_INVALID_INPUT_CODE, Constants.GENERAL_INVALID_INPUT_MSG),
	/** When an entity has been modified by other thread*/
	GENERAL_STALE_OBJECT_STATE_EXCEPTION(Constants.STALE_OBJECT_STATE_REQUEST_CODE, Constants.STALE_OBJECT_STATE_REQUEST_MSG),
	/** When an entity has been modified by other thread*/
	ACCOUNT_INCONSISTENT(Constants.ACCOUNT_INCONSISTENT_ERROR_CODE, Constants.ACCOUNT_INCONSISTENT_ERROR_MSG),
	/** When an entity has reached to minimum balance limit*/
	ACCOUNT_MINIMUM_BALANCE_ERROR(Constants.ACCOUNT_MINIMUM_BALANCE_ERROR_CODE, Constants.ACCOUNT_MINIMUM_BALANCE_ERROR_MSG),
	/** When an entity has been modified by other thread*/
	INVALID_CREDIT_DEBIT_ERROR(Constants.INVALID_CREDIT_DEBIT_ERROR_CODE, Constants.INVALID_CREDIT_DEBIT_ERROR_MSG),
	TRANSACTION_ADDITIONAL_FIELD_REQUIRED_ERROR(Constants.TRANSACTION_ADDITIONAL_FIELD_REQUIRED_CODE,
			Constants.TRANSACTION_ADDITIONAL_FIELD_REQUIRED_MSG),
	BENEFICIARY_ADDITIONAL_FIELD_REQUIRED_ERROR(Constants.BENEFICIARY_ADDITIONAL_FIELD_REQUIRED_CODE,
			Constants.BENEFICIARY_ADDITIONAL_FIELD_REQUIRED_MSG),
	TRANSACTION_REJECTED_IDM_ERROR(Constants.TRANSACTION_REJECTED_IDM_ERROR_CODE,
			Constants.TRANSACTION_REJECTED_IDM_ERROR_MSG),
	QUICKBOOKS_LOGOUT(Constants.QUICK_BOOKS_CLIENT_TOKEN_EXPIRE, Constants.QUICK_BOOKS_CLIENT_TOKEN_EXPIRE_MSG),
	FORGOT_PASSWORD_USERNAME_ERROR(Constants.FORGOT_PASSWORD_USERNAME_ERROR_CODE, Constants.SUCCESS_MSG);


    String responseCode;
	String responseMessage;

	public static class Constants {
		public static final String BENEFICIARY_ADDITIONAL_FIELD_REQUIRED_MSG = "beneficiary additional fields are missing";
		public static final String BENEFICIARY_ADDITIONAL_FIELD_REQUIRED_CODE = "59905003";
		public static final String TRANSACTION_ADDITIONAL_FIELD_REQUIRED_MSG = "transaction additional fields are missing";
		public static final String TRANSACTION_ADDITIONAL_FIELD_REQUIRED_CODE = "59905004";
		public static final String SUCCESS_CODE = "00000000";
		public static final String SUCCESS_MSG = "com.stayeaze.general.success";

		// This code is general code but it should be specific according to module
		public static final String TX_DUPLICATE_REQUEST_CODE = "11102001";
		public static final String DUPLICATE_REQUEST_CODE = "19903001";
		public static final String DUPLICATE_REQUEST_MSG = "com.stayeaze.general.duplicate.request";

		public static final String GENERAL_INVALID_INPUT_CODE = "19903009";
		public static final String GENERAL_INVALID_INPUT_MSG = "com.stayeaze.general.invalid.input";
		public static final String GENERAL_INVALID_BLANK_REQUEST = "com.stayeaze.general.blank.request";
		public static final String GENERAL_NOT_FOUND_CODE = "19904006";
		public static final String GENERAL_NOT_FOUND_MSG = "com.stayeaze.general.not.found";
		public static final String INVALID_SHARE = "com.stayeaze.general.invalidOwnershipShare";

		public static final String GENERAL_SERVICE_NOT_AVAILABLE_CODE = "19906002";
		public static final String GENERAL_SERVICE_NOT_AVAILABLE_MSG = "com.stayeaze.general.service.notAvailable";

		public static final String GENERAL_INVALID_REQUEST_OBJECT_CODE = "19903009";
		public static final String GENERAL_INVALID_REQUEST_OBJECT_MSG = "com.stayeaze.general.invalid.request";

		public static final String SYSTEM_ERROR_CODE = "19906002";
		public static final String SYSTEM_ERROR_MSG = "com.stayeaze.general.system.error";

		public static final String GENERAL_INTERNAL_PROCESSING_ERROR_CODE = "19906002";
		public static final String GENERAL_INTERNAL_PROCESSING_ERROR_MSG = "com.stayeaze.general.system.error";

		public static final String GENERAL_INVALID_LOGIN_CREDENTIALS_CODE = "19906002";
		public static final String BAD_CREDENTIALS = "com.stayeaze.auth.bad.credentials";
		public static final String INSUFFICIENT_PERMISSION = "com.stayeaze.auth.insufficient.permission";

		public static final String INVALID_TOKEN_CODE = "19903009";
		public static final String INVALID_UPLOAD_TOKEN = "com.stayeaze.token.invalid.upload";
		public static final String EXPIRED_TOKEN_CODE = "19903009";
		public static final String EXPIRED_UPLOAD_TOKEN = "com.stayeaze.token.expired.upload";

		public static final String INVALID_COMPANY_CODE = "com.stayeaze.b2b.invalid.companyCode";

		public static final String ACCOUNT_LOCKED_CODE = "10105005";
		public static final String ACCOUNT_LOCKED_MSG = "com.stayeaze.b2b.resetpassword.account.locked.msg";

		public static final String ADDITIONAL_DATA_REQUIRED = "19902001";
		public static final String ADDITIONAL_DATA_REQUIRED_MSG = "com.stayeaze.general.additional_data_required";

		public static final String GENERAL_PARTIAL_SUCCESS_CODE = "19902006";
		public static final String GENERAL_PARTIAL_SUCCESS_MSG = "com.stayeaze.general.partial_success";

		public static final String ACCESS_DENIED_CODE = "10104101";
		public static final String ACCESS_DENIED_MSG = "com.stayeaze.auth.responseMsg.access_denied";
		public static final String CLIENT_pJDaJDsJDs_CHANGE_REQUIRE = "10105001";
		public static final String CLIENT_TOKEN_EXPIRE = "10107010";
		public static final String QUICK_BOOKS_CLIENT_TOKEN_EXPIRE = "10107011";
        public static final String TRANSACTION_HOLD = "11102002";
		public static final String TRANSACTION_PRE_HOLD = "11104006";
        public static final String INVALID_AUTH_TOKEN_CODE="10104007";
		public static final String INVALID_AUTH_TOKEN_MSG="com.stayeaze.auth.responseCode.invalid_token";

		public static final String STALE_OBJECT_STATE_REQUEST_CODE = "19905009";
		public static final String STALE_OBJECT_STATE_REQUEST_MSG="com.stayeaze.general.stale_object_state";
		public static final String ACCOUNT_INCONSISTENT_ERROR_CODE = "11105001";
		public static final String ACCOUNT_INCONSISTENT_ERROR_MSG = "Account is Inconsistent. Please Contact to App Support Team.";
		public static final String ACCOUNT_MINIMUM_BALANCE_ERROR_CODE = "11103001";
		public static final String ACCOUNT_MINIMUM_BALANCE_ERROR_MSG = "Insufficient Balance in Account. Please Request to Add Balance in the Account.";
		public static final String INVALID_CREDIT_DEBIT_ERROR_CODE = "11103002";
		public static final String INVALID_CREDIT_DEBIT_ERROR_MSG = "Invalid Credit or Debit Amount.";
		public static final String LIMIT_EXPIRED_CODE = "51104001";
        public static final String LIMIT_EXPIRED_MSG = "com.stayeaze.txLimit.expired";
        public static final String INVALID_OPTION_SIZE = "invalid option size";
		public static final String INVALID_OPTION = "invalid option";
		public static final String GENERAL_INVALID_LENGTH = "invalid length";
		public static final String GENERAL_INVALID_FORMAT = "invalid format";
		public static final String GENERAL_INVALID_FILE_SIZE = "invalid file size";
		public static final String QUICK_BOOKS_CLIENT_TOKEN_EXPIRE_MSG = "The Quickbooks connection expired, if you wish to continue using your data please enter your Quickbooks credentials";
		public static final String CANCELPENDING_CODE = "11102005";

		public static final String TRANSACTION_REJECTED_IDM_ERROR_CODE = "19905001";
		public static final String TRANSACTION_REJECTED_IDM_ERROR_MSG = "com.stayeaze.business.responseMsg.tx_rejected_idm";
		public static final String FORGOT_PASSWORD_USERNAME_ERROR_CODE = "19902002";
		private Constants() {
		}
	}
}
