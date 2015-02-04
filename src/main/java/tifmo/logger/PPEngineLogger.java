package tifmo.logger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by bdthinh on 12/19/14.
 */
public class PPEngineLogger {
	private static Logger _logger;
	public static void init(){
		_logger = LogManager.getLogger("Hello world");
	}
	public static void main(String[] args) {
		_logger.info("Hello, world");
	}
}
