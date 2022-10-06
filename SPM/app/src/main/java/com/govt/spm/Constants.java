package com.govt.spm;

public class Constants {
    public static final String ROOT_URL="http://ec2-3-7-135-193.ap-south-1.compute.amazonaws.com/SPA_Webservices/v1/";
    public static final String LOGIN = ROOT_URL+"authenticateUser.php";
    public static final String REGISTER_USER = ROOT_URL+"registerUser.php";
    public static final String CREATE_USER_PROFILE = ROOT_URL+"createUserProfile.php";
    public static final String UPDATE_USER_PROFILE = ROOT_URL+"updateUserProfile.php";

    public static final String GET_COUNTRY = ROOT_URL+"getCountriesList.php";
    public static final String GET_STATE = ROOT_URL+"getStatesList.php";
    public static final String GET_CITY = ROOT_URL+"getCitiesList.php";
    public static final String GET_COUNTRY_STATE_CITY = ROOT_URL+"getCountryStateCityList.php";

    public static final String GET_UNIV = ROOT_URL+"getUnivList.php";
    public static final String GET_COLLEGES = ROOT_URL+"getCollegesList.php";
    public static final String GET_DEPARTMENTS = ROOT_URL+"getCollegeWiseDeptList.php";

    public static final String GET_COMPANIES = ROOT_URL+"getCompaniesList.php";
    public static final String GET_JOB_LIST = ROOT_URL+"getJobsList.php";
    public static final String MANAGE_COMPANY_PROFILE = ROOT_URL+"manageCompanyProfile.php";
    public static final String MANAGE_JOB_DETAIL = ROOT_URL+"manageJobDetails.php";
    public static final String GET_COMPANY_PROFILE = ROOT_URL+"getCompanyProfile.php";
    public static final String GET_JOB_DETAIL = ROOT_URL+"getJobDetails.php";

    public static final String GET_STUDENT_LIST = ROOT_URL+"getStudentsList.php";
}
