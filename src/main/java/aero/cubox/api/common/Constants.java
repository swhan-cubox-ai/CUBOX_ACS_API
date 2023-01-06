package aero.cubox.api.common;

public class Constants {

    private Constants() {
    }

    public class Profile {

        private Profile() {
        }

        public static final String DEVELOPMENT = "dev";
        public static final String PRODUCTION = "prod";

    }

    public class API {

        private API() {
        }

        // NO-AUTH

        public static final String API_ACS_PREFIX = "/acs/v1";
        public static final String API_AUTH = "/auth";
        public static final String API_CMMNCD = "/cmmncd";
        public static final String API_CMMN = "/acs/common";

        public static final String API_DEMO = "/demo";

        public static final String API_SYNC = "/sync";
        public static final String API_EMP =  "/emp"; // T_EMP, T_FACE_FEATURE
        public static final String API_CARD =  "/card"; // T_CARD
        public static final String API_FACE =  "/face"; // 단건처리 faceId
        public static final String API_AUTHMAP =  "/authmap"; // TO_DO 1:1
        public static final String API_WB =  "/wb"; // TO_DO 1:1

        public static final String API_ACTLOG = "/actlog";


        // AUTH
        public static final String API_ACSADM_PREFIX = "/acsadm/v1";

        public static final String API_ACSADM_USER = API_ACSADM_PREFIX + "/user";

    }


}
