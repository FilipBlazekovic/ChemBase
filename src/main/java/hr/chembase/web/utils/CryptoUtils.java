package hr.chembase.web.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import hr.chembase.web.db.SQLStatements;

public class CryptoUtils {
	
	private static final long PERIOD_12_HOURS_IN_MS = (1000 * 60 * 60 * 12);

	public static final int SESSION_ID_VALID_AND_USER_ADMIN = 0;
	public static final int SESSION_ID_VALID = 1;
	public static final int SESSION_ID_INVALID = 2;

	/* ______________________________________________________________________________________________________________ */

	public static String generateSalt()
	{
		final SecureRandom randomGenerator = new SecureRandom();
		final byte[] randomBytes = new byte[2];
		randomGenerator.nextBytes(randomBytes);
		return (convertByteArrayToHexString(randomBytes));
	}
	
	/* ______________________________________________________________________________________________________________ */
	
	public static String generatePasswordHash(final String password, final String salt)
	{
		String passwordHash = null;
		try
		{
			final MessageDigest digester = MessageDigest.getInstance("SHA-256");	        
			digester.update(salt.getBytes("ISO-8859-1"));
			digester.update(password.getBytes("ISO-8859-1"));
	        byte[] hash = digester.digest();
	        passwordHash = convertByteArrayToHexString(hash);
		}
		catch (Exception ex) {}
		return passwordHash;
	}

	/* ______________________________________________________________________________________________________________ */

	public static String generateSessionID()
	{	
		String sessionID = null;	
		try
		{
			final String nanotimePrefix = String.valueOf(System.nanoTime());
			
			final SecureRandom randomGenerator = new SecureRandom();
			final byte[] randomBytes = new byte[16];
			randomGenerator.nextBytes(randomBytes);
			
			final MessageDigest sha256Digester = MessageDigest.getInstance("SHA-256");
			sha256Digester.update(nanotimePrefix.getBytes("ISO-8859-1"));
			sha256Digester.update(randomBytes);
			final byte[] rawResult = sha256Digester.digest();
			
			sessionID = convertByteArrayToHexString(rawResult);
		}
		catch (Exception ex) {}
		
		return sessionID;
	}

	/* ______________________________________________________________________________________________________________ */

	private static String convertByteArrayToHexString(byte[] raw)
	{
	   final StringBuilder builder = new StringBuilder();
	   for (int i = 0; i < raw.length; i++)
	   {
		   builder.append(String.format("%02X", raw[i]));
	   }
	   return builder.toString();
	}

	/* ______________________________________________________________________________________________________________ */

	/* If validateLoginCredentials return true,
	 * login credentials are correct and a new
	 * sessionID is created for that user
	 */

	public static boolean validateLoginCredentials(final Connection connection, final String username, final String password)
	{
		boolean validationStatus = false;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.GET_LOGIN_CREDENTIALS_SQL);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			if (resultSet.next())
			{
				final String dbSalt = resultSet.getString(1);
				final String dbPasswordHash = resultSet.getString(2);				
				final String passwordHashString = generatePasswordHash(password, dbSalt);

				if (passwordHashString != null && passwordHashString.equals(dbPasswordHash))
				{
					validationStatus = true;
					try
					{
						if (resultSet != null) resultSet.close();
						if (statement != null) statement.close();
					}
					catch (Exception ex) {}
					
					statement = connection.prepareStatement(SQLStatements.POST_LOGIN_RESET_SQL);
					statement.setString(1, username);
					statement.execute();
				}
			}
		}
		catch (Exception ex)
		{
			validationStatus = false;
			throw new RuntimeException(ex);
		}
		finally
		{
			try
			{
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			}
			catch (Exception ex) {}
		}
		return validationStatus;
	}

	/* ______________________________________________________________________________________________________________ */

	public static boolean deleteSessionID(final Connection connection, final String sessionID)
	{
		boolean deleteStatus = true;
		PreparedStatement statement = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.DELETE_SESSION_SQL);
			statement.setString(1, sessionID);
			statement.execute();
		}
		catch (Exception ex)
		{
			deleteStatus = false;
			throw new RuntimeException(ex);
		}
		finally
		{
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
		}
		return deleteStatus;
	}

	/* ______________________________________________________________________________________________________________ */
	
	public static boolean deleteSessionIDForUser(final Connection connection, final String username)
	{
		boolean deleteStatus = true;
		PreparedStatement statement = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.DELETE_SESSION_ID_FOR_USER_SQL);
			statement.setString(1, username);
			statement.execute();
		}
		catch (Exception ex)
		{
			deleteStatus = false;
			throw new RuntimeException(ex);
		}
		finally
		{
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
		}
		return deleteStatus;
	}

	/* ______________________________________________________________________________________________________________ */

	public static boolean insertSessionIDRecord(final Connection connection, final String username, final String sessionID)
	{
		boolean insertStatus = true;
		PreparedStatement statement = null;
		try
		{			
			statement = connection.prepareStatement(SQLStatements.INSERT_SESSION_ID_SQL);
			statement.setString(1, username);
			statement.setString(2, sessionID);
			statement.execute();
		}
		catch (Exception ex)
		{
			insertStatus = false;
			throw new RuntimeException(ex);
		}
		finally
		{
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
		}
		return insertStatus;
	}

	/* ______________________________________________________________________________________________________________ */

	public static boolean incrementInvalidPasswordCounter(final Connection connection, final String username)
	{
		boolean updateStatus = true;
		PreparedStatement statement = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.INCREMENT_INVALID_PASSWORD_COUNTER_FOR_USER_SQL);
			statement.setString(1, username);
			statement.execute();
		}
		catch (Exception ex)
		{
			updateStatus = false;
			throw new RuntimeException();
		}
		finally
		{
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
		}
		return updateStatus;
	}

	/* ______________________________________________________________________________________________________________ */

	public static boolean lockAccountIfConditionsAreMet(final Connection connection, final String username)
	{
		boolean accountLocked = false;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.LOCK_ACCOUNT_IF_CONDITIONS_ARE_MET_SQL);
			statement.setString(1, username);
			statement.execute();
			
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
			
			statement = connection.prepareStatement(SQLStatements.CHECK_IF_ACCOUNT_LOCKED_SQL);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			if (resultSet.next())
			{
				final Boolean userLocked = (Boolean) resultSet.getBoolean(1);
				if (userLocked)
					accountLocked = true;
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException();
		}
		finally
		{
			try
			{
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			}
			catch (Exception ex) {}
		}
		return accountLocked;
	}

	/* ______________________________________________________________________________________________________________ */
	/* ______________________________________________________________________________________________________________ */
	/* ______________________________________________________________________________________________________________ */
	/* ______________________________________________________________________________________________________________ */
	
	public static int validateSessionID(Connection connection, final String sessionID)
	{
		int returnValue = SESSION_ID_INVALID;

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{			
			statement = connection.prepareStatement(SQLStatements.VALIDATE_SESSION_ID_SQL);
			statement.setString(1, sessionID);
			resultSet = statement.executeQuery();
			
			if (resultSet.next())
			{
				final String username = (String) resultSet.getString(1);
				final Timestamp sessionCreationTimestamp = (Timestamp) resultSet.getTimestamp(2);
				final Timestamp sessionLastAccessedTimestamp = (Timestamp) resultSet.getTimestamp(3);

				final long currentTimeInMilliseconds = System.currentTimeMillis();
				final long sessionCreationTimeInMilliseconds = sessionCreationTimestamp.getTime();
				final long sessionLastAccessedTimeInMilliseconds = sessionLastAccessedTimestamp.getTime();
				
				if ((currentTimeInMilliseconds - sessionLastAccessedTimeInMilliseconds) > PERIOD_12_HOURS_IN_MS)
				{
					returnValue = SESSION_ID_INVALID;
				}
				else
				{
					if (username.toUpperCase().equals("ADMIN"))
						returnValue = SESSION_ID_VALID_AND_USER_ADMIN;
					else
						returnValue = SESSION_ID_VALID;
				}
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			try
			{
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			}
			catch (Exception ex) {}
		}

		/* Updating the session ID
		 * last accessed timestamp
		 * with each valid request
		 */
		if (returnValue == SESSION_ID_VALID) { updateSessionID(connection, sessionID); }
		
		return returnValue;
	}
	
	/* ______________________________________________________________________________________________________________ */

	public static boolean updateSessionID(final Connection connection, final String sessionID)
	{
		boolean updateStatus = true;
		PreparedStatement statement = null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.UPDATE_SESSION_SQL);
			statement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			statement.setString(2, sessionID);
			statement.execute();
		}
		catch (Exception ex)
		{
			updateStatus = false;
			throw new RuntimeException(ex);
		}
		finally
		{
			try { if (statement != null) statement.close(); }
			catch (Exception ex) {}
		}
		return updateStatus;
	}
	
	/* ______________________________________________________________________________________________________________ */

	public static boolean checkIfAccountLocked(final Connection connection, final String username)
	{
		boolean accountLocked 		= false;
		PreparedStatement statement = null;
		ResultSet resultSet 		= null;
		try
		{
			statement = connection.prepareStatement(SQLStatements.CHECK_IF_ACCOUNT_LOCKED_SQL);
			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			if (resultSet.next())
				accountLocked = true;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			try
			{
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			}
			catch (Exception ex) {}
		}
		return accountLocked;
	}

	/* ______________________________________________________________________________________________________________ */
	
}
