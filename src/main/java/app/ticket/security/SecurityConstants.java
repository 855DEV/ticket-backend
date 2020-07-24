package app.ticket.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SEARCH_URL = "/search";
    public static final String RECOMMEND_URL = "/recommend";
    public static final String SIGN_UP_URL = "/user/register";
    public static final String TICKET_URL = "/ticket/**";
    public static final String ORDERS_URL = "/orders/**";
    public static final String USER_URL = "/user/";
}
