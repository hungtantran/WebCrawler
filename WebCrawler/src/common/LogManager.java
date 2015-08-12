package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;

public class LogManager {
	private static LogManager genericlogManager = new LogManager(Globals.DEFAULTLOGDIRECTORY, Globals.DEFAULTLOGFILEPREFIX);
	
	private boolean m_defaultWriteToDisk = true;
	private String m_logDir = null; // Logging directory
	private String m_baseFileName = null; // Base file name
	private String m_currentLogFileName = null; // Current log file name
	private PrintWriter m_currentLogFileWriter = null; // Current writer to the current log file name
	private String m_currentLogFileDate = null;
	private Integer m_currentLogFileHour = null;
	private ReentrantLock m_mutex = null;
	private Long m_logFileSize = null;

	public LogManager(String directory, String baseFileName, boolean defaultWriteToDisk) {
		this(directory, baseFileName);
		this.m_defaultWriteToDisk = defaultWriteToDisk;
	}
	
	public LogManager(String directory, String baseFileName) {
		this.m_logDir = directory;
		this.m_baseFileName = baseFileName;
		this.m_mutex = new ReentrantLock();
		
		// Exit if can't create directory/file and the directory/file hasn't already been existed
		File dir = Helper.createDir(this.m_logDir);
		if (dir == null) {
			System.out.println("Can't create log folder");
			System.exit(1);
		}
		
		m_currentLogFileDate = Helper.getCurrentDate();
		m_currentLogFileHour = Helper.getCurrentHour();
		m_currentLogFileName = getLogFileName(m_currentLogFileDate, m_currentLogFileHour);
		
		File file = Helper.createFile(m_currentLogFileName);
		if (file == null) {
			System.out.println("File " + m_currentLogFileName + " can't be created");
			System.exit(1);
		}
		
		m_logFileSize = file.getTotalSpace();
		
		// Create a buffer writer for the log write, exit if creation fails
		try {
			m_currentLogFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(m_currentLogFileName, true)));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getLogFileName(String date, int hour) {
		return this.m_logDir + Globals.PATHSEPARATOR + m_baseFileName + "." + date + "-" + hour + ".log";
	}

	// Write log with default writeToDisk value
	public boolean writeLog(String log) {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		// 0 is getStackTrace, 1 is the writeLog function, 2 is the writeGenericLog, 3 is writeLog's caller
		String functionName = ste[3].getMethodName();
		String fileName = Thread.currentThread().getStackTrace()[3].getFileName();
		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

		return this.writeLog(fileName, lineNumber, functionName, log, this.m_defaultWriteToDisk);
	}

	public boolean writeLog(String fileName, int lineNumber, String functionName, String log) {
		return this.writeLog(fileName, lineNumber, functionName, log, this.m_defaultWriteToDisk);
	}
	
	public boolean writeLog(String log, boolean writeToDisk) {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		// 0 is getStackTrace, 1 is the writeLog function, 2 is the writeLog's caller
		String functionName = ste[3].getMethodName();
		String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		
		return this.writeLog(fileName, lineNumber, functionName, log, writeToDisk);
	}

	public boolean writeLog(String fileName, int lineNumber, String functionName, String log, boolean writeToDisk) {
		if (this.m_logDir == null || this.m_baseFileName == null || functionName == null || log == null) {
			return false;
		}
		
		this.m_mutex.lock();
		
		try {
			String logLine = "[" + fileName + ":" + lineNumber + "] [" + Helper.getCurrentDate() + "] [" + Helper.getCurrentTime() + "] [" + functionName + "]: [" + Thread.currentThread().getId() + "] " + log;
			
			System.out.println("Log: " + logLine);
			if (writeToDisk) {
				try {
					Integer currentHour = Helper.getCurrentHour();
					String currentDate = Helper.getCurrentDate();

					// If the log name changes already, close the current log file and open a new one
					if (currentHour != this.m_currentLogFileHour || currentDate != this.m_currentLogFileDate) {
						this.m_currentLogFileWriter.close();
						this.m_currentLogFileDate = currentDate;
						this.m_currentLogFileHour = currentHour;
						this.m_currentLogFileName = this.getLogFileName(this.m_currentLogFileDate, this.m_currentLogFileHour);
						this.m_currentLogFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(m_currentLogFileName, true)));
						this.m_logFileSize = 0L;
					}
					
					this.m_currentLogFileWriter.println(logLine);
					
					this.m_logFileSize += logLine.length();
					if (this.m_logFileSize > Globals.MAXLOGBUFFERSIZEINMB * 1024 * 1024) {
						System.out.println("Flush data size " + this.m_logFileSize + " to disk");
						this.m_currentLogFileWriter.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
	
			return true;
		} finally {
			this.m_mutex.unlock();
		}
	}
	
	public static boolean writeGenericLog(String log) {
		return LogManager.genericlogManager.writeLog(log);
	}
}
