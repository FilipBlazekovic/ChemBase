package hr.chembase.web.utils;

public class Utils {

	public static String generateStackTrace(Exception ex)
	{
		final StringBuilder logErrorBuilder = new StringBuilder();
		logErrorBuilder.append("*** EXECEPTION ***\n");
		logErrorBuilder.append("Error message: " + ex.getMessage() + "\n");
		final StackTraceElement[] stackTrace = ex.getStackTrace();
		for (int i = 0; i < stackTrace.length; i++)
		{
			logErrorBuilder.append(stackTrace[i].toString());
		}
		return logErrorBuilder.toString();
	}
}
