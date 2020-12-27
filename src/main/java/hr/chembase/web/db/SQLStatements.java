package hr.chembase.web.db;

public class SQLStatements {

    public static final String GET_LOGIN_CREDENTIALS_SQL = "SELECT usersalt, userpass FROM users WHERE username = ?";

    public static final String POST_LOGIN_RESET_SQL = "UPDATE USERS SET " +
                                                      "userlocked = false, " +
                                                      "userlockdate = NULL, " +
                                                      "userinvalidpasscount = 0 " +
                                                      "WHERE username = ?";

    public static final String DELETE_SESSION_SQL = "DELETE FROM sessions WHERE sesssessionid = ?";
    
    public static final String UPDATE_SESSION_FOR_USER_SQL = "DELETE FROM sessions WHERE sessuserid = (SELECT userid from users where username = ?)";   

    public static final String INSERT_SESSION_ID_SQL = "INSERT INTO sessions(sessuserid, sesssessionid) " +
                                                       "VALUES((SELECT userid from users where username = ?), ?)";
    
    public static final String INCREMENT_INVALID_PASSWORD_COUNTER_FOR_USER_SQL = "UPDATE users SET " +
                                                                                 "userinvalidpasscount = (userinvalidpasscount+1) " +
                                                                                 "WHERE username = ?";

    public static final String LOCK_ACCOUNT_IF_CONDITIONS_ARE_MET_SQL = "UPDATE users SET " +
                                                                        "userlocked = true, " +
                                                                        "userlockdate = NOW() " +
                                                                        "WHERE username = ? " +
                                                                        "AND userinvalidpasscount >= 5";

    public static final String CHECK_IF_ACCOUNT_LOCKED_SQL = "SELECT userid FROM users WHERE username = ? AND userlocked = true";

    public static final String VALIDATE_SESSION_ID_SQL = "SELECT " +
                                                         "username, " +
                                                         "sesscreationtime AS session_creation_tstamp, " +
                                                         "sesslastaccesstime AS session_last_accessed_tstamp " +
                                                         "FROM sessions " +
                                                         "INNER JOIN USERS ON sessuserid = userid " +
                                                         "WHERE sesssessionid = ?";
    
    public static final String UPDATE_SESSION_SQL = "UPDATE sessions " +
                                                    "SET sesslastaccesstime = ? " +
                                                    "WHERE sesssessionid = ?";

    public static final String DELETE_SESSION_ID_FOR_USER_SQL = "DELETE FROM sessions " +
                                                                "WHERE sessuserid = (SELECT userid from users where username = ?)";

    /* ________________________________________________________________________________________________________________________ */

    public static final String GET_USERS_SQL = "SELECT " +
                                               "userid AS user_id, " +
                                               "username AS username, " +
                                               "CASE userlocked WHEN true THEN 'YES' ELSE 'NO' END AS user_locked, " +
                                               "TO_CHAR(userlockdate, 'YYYY/MM/DD') AS lockdate " +
                                               "FROM users ORDER BY username";
    
    public static final String GET_LOCATIONS_SQL = "SELECT locid, locname FROM locations ORDER BY locname";

    public static final String GET_CHEMICALS_SQL = "SELECT " +
                                                   "x.chemid AS chemical_id, " +
                                                   "x.chemname AS chemical_name, " +
                                                   "x.chembruttoformula AS brutto_formula, " +
                                                   "x.chemmolarmass AS molar_mass, " +
                                                   "x.chemamount AS amount, " +
                                                   "x.chemunit AS unit, " +
                                                   "y.locname AS storage_location, " +
                                                   "x.chemmanufacturer AS manufacturer, " +
                                                   "x.chemsupplier AS supplier, " +
                                                   "TO_CHAR(x.chementrydate, 'YYYY/MM/DD') AS entry_date, " +
                                                   "x.cheminfo AS additional_info " +
                                                   "FROM chemicals x " +
                                                   "INNER JOIN locations y ON x.chemstoragelocation = y.locid " +
                                                   "ORDER BY x.chementrydate DESC " +
                                                   "LIMIT 1000";

    /* ________________________________________________________________________________________________________________________ */
    
    public static final String SEARCH_CHEMICALS_CORE_SQL = "SELECT " +
                                                           "x.chemid AS chemical_id, " +
                                                           "x.chemname AS chemical_name, " +
                                                           "x.chembruttoformula AS brutto_formula, " +
                                                           "x.chemmolarmass AS molar_mass, " +
                                                           "x.chemamount AS amount, " +
                                                           "x.chemunit AS unit, " +
                                                           "y.locname AS storage_location, " +
                                                           "x.chemmanufacturer AS manufacturer, " +
                                                           "x.chemsupplier AS supplier, " +
                                                           "TO_CHAR(x.chementrydate, 'YYYY/MM/DD') AS entry_date, " +
                                                           "x.cheminfo AS additional_info " +
                                                           "FROM chemicals x " +
                                                           "INNER JOIN locations y ON x.chemstoragelocation = y.locid " +
                                                           "WHERE 1=1";

    public static final String DELETE_CHEMICAL_SQL = "DELETE FROM chemicals WHERE chemid = ?";
    
    public static final String ADD_CHEMICAL_SQL = "INSERT INTO chemicals(" +
                                                  "chemname, " +
                                                  "chembruttoformula, " +
                                                  "chemmolarmass, " +
                                                  "chemamount, " +
                                                  "chemunit, " +
                                                  "chemstoragelocation, " +
                                                  "chemmanufacturer, " +
                                                  "chemsupplier, " +
                                                  "cheminfo) " +
                                                  "VALUES (?,?,?,?,?,?,?,?,?)";
    
    public static final String EDIT_CHEMICAL_SQL = "UPDATE chemicals SET " +
                                                   "chemname = ?, " +
                                                   "chembruttoformula = ?, " +
                                                   "chemmolarmass = ?, " +
                                                   "chemamount = ?, " +
                                                   "chemunit = ?, " +
                                                   "chemstoragelocation = ?, " +
                                                   "chemmanufacturer = ?, " +
                                                   "chemsupplier = ?, " +
                                                   "cheminfo = ? " +
                                                   "WHERE chemid = ?";
    
    public static final String GET_EDITED_CHEMICAL = "SELECT " +
                                                     "x.chemid AS chemical_id, " +
                                                     "x.chemname AS chemical_name, " +
                                                     "x.chembruttoformula AS brutto_formula, " +
                                                     "x.chemmolarmass AS molar_mass, " +
                                                     "x.chemamount AS amount, " +
                                                     "x.chemunit AS unit, " +
                                                     "y.locname AS storage_location, " +
                                                     "x.chemmanufacturer AS manufacturer, " +
                                                     "x.chemsupplier AS supplier, " +
                                                     "TO_CHAR(x.chementrydate, 'YYYY/MM/DD') AS entry_date, " +
                                                     "x.cheminfo AS additional_info " +
                                                     "FROM chemicals x " +
                                                     "INNER JOIN locations y ON x.chemstoragelocation = y.locid " +
                                                     "WHERE x.chemid = ?";

    public static final String GET_LAST_ADDED_CHEMICAL = "SELECT " +
                                                         "x.chemid AS chemical_id, " +
                                                         "x.chemname AS chemical_name, " +
                                                         "x.chembruttoformula AS brutto_formula, " +
                                                         "x.chemmolarmass AS molar_mass, " +
                                                         "x.chemamount AS amount, " +
                                                         "x.chemunit AS unit, " +
                                                         "y.locname AS storage_location, " +
                                                         "x.chemmanufacturer AS manufacturer, " +
                                                         "x.chemsupplier AS supplier, " +
                                                         "TO_CHAR(x.chementrydate, 'YYYY/MM/DD') AS entry_date, " +
                                                         "x.cheminfo AS additional_info " +
                                                         "FROM chemicals x " +
                                                         "INNER JOIN locations y ON x.chemstoragelocation = y.locid " +
                                                         "WHERE x.chemname = ? " +
                                                         "AND x.chembruttoformula = ? " +
                                                         "AND x.chemmolarmass = ? " +
                                                         "AND x.chemamount = ? " +
                                                         "AND x.chemunit = ? " +
                                                         "AND x.chemstoragelocation = ? " +
                                                         "AND x.chemmanufacturer = ? " +
                                                         "AND x.chemsupplier = ? " +
                                                         "AND x.cheminfo = ? " + 
                                                         "ORDER BY x.chementrydate DESC LIMIT 1";

    /* ________________________________________________________________________________________________________________________ */
    
    public static final String CHECK_IF_DELETABLE_LOCATION_SQL = "SELECT chemid FROM chemicals WHERE chemstoragelocation = ? LIMIT 1";

    public static final String CHECK_IF_DUPLICATE_LOCATION_SQL = "SELECT locid FROM locations WHERE locname = ?";
    
    public static final String DELETE_LOCATION_SQL = "DELETE FROM locations WHERE locid = ?";

    public static final String ADD_LOCATION_SQL = "INSERT INTO locations(locname) VALUES(?)";

    public static final String EDIT_LOCATION_SQL = "UPDATE locations SET locname = ? WHERE locid = ?";  

    /* ________________________________________________________________________________________________________________________ */
    
    public static final String CHECK_IF_DELETABLE_USER_SQL = "SELECT userid FROM users WHERE userid = ? AND username = 'admin'";

    public static final String CHECK_IF_DUPLICATE_USER_SQL = "SELECT userid FROM users WHERE username = ?";
    
    public static final String DELETE_USER_SQL = "DELETE FROM users WHERE userid = ?";
    
    public static final String ADD_USER_SQL = "INSERT INTO users(username,userpass,usersalt) VALUES (?,?,?)";
    
    public static final String EDIT_USER_SQL = "UPDATE users SET " +
                                               "userpass = ?, " +
                                               "usersalt = ?, " +
                                               "userlocked = false, " +
                                               "userinvalidpasscount = 0, " +
                                               "userlockdate = NULL " +
                                               "WHERE userid = ?";

    /* ________________________________________________________________________________________________________________________ */

}
