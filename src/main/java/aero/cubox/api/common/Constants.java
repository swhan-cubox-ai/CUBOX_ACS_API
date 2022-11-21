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

        public static final String API_DEMO = "/demo";

        public static final String API_MDM = "/mdm";

        public static final String API_ACTLOG = "/actlog";

        // AUTH
        public static final String API_ACSADM_PREFIX = "/acsadm/v1";

        public static final String API_USER = "/user";

    }


}
