CREATE TABLE locations
(
    locid SERIAL PRIMARY KEY,
    locname VARCHAR(128) NOT NULL
);

CREATE TABLE chemicals
(
    chemid SERIAL PRIMARY KEY,
    chemname VARCHAR(128) NOT NULL,
    chembruttoformula VARCHAR(64) NOT NULL,
    chemmolarmass VARCHAR(64) NOT NULL,
    chemamount REAL NOT NULL,
    chemunit VARCHAR(10) NOT NULL,
    chemstoragelocation INT NOT NULL REFERENCES locations(locid) ON DELETE CASCADE,
    chemmanufacturer VARCHAR(128),
    chemsupplier VARCHAR(128),
    chementrydate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    cheminfo TEXT
);

CREATE TABLE users
(
    userid SERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    userpass VARCHAR(64) NOT NULL,
    usersalt VARCHAR(4) NOT NULL,
    usercreatedate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    userlockdate TIMESTAMP WITHOUT TIME ZONE,
    userlocked BOOLEAN NOT NULL DEFAULT false,
    userinvalidpasscount INT NOT NULL DEFAULT 0
);

CREATE TABLE sessions
(   
    sessid SERIAL PRIMARY KEY,
    sessuserid INT NOT NULL REFERENCES users(userid) ON DELETE CASCADE,
    sesssessionid VARCHAR(64) NOT NULL,
    sesscreationtime TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    sesslastaccesstime TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_username ON users(username);
CREATE INDEX is_sessuserid ON sessions(sessuserid);
CREATE INDEX ix_sesssessionid ON sessions(sesssessionid);
CREATE INDEX ix_locname ON locations(locname);
CREATE INDEX ix_chemname ON chemicals(chemname);
CREATE INDEX ix_chemstoragelocation ON chemicals(chemstoragelocation);

-- Creates a user with username 'admin' and default password 'admin'
-- This user is mandatory, but the password can be changed through GUI after first login
INSERT INTO USERS(username, userpass, usersalt, usercreatedate, userlockdate, userlocked, userinvalidpasscount)
VALUES ('admin', '8F9640478EB6A4A2C2FD67E900EB0F1A8EF16A23338E8FA5987592F4C5D70211', 'FFFF', now(), null, false, 0);
