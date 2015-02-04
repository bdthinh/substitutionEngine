package tifmo.en;

/**
 * Created by bdthinh on 12/6/14.
 */
public class EnFactory {
	private static int _dimMikolov = 300;
	private static int _dimTurian = 50;
	private static int _dimDEPS = 300;
	private static int _dimGlove = 300;
	private static EnGlove _glove = new EnGlove();
	private static EnMikolov _mikolov = new EnMikolov();
	private static EnTurian _turian = new EnTurian();
	private static EnDEPS _DEPS = new EnDEPS();
	private static EnDIRT _DIRT = new EnDIRT();
	private static EnGeo _GEO = new EnGeo();
	private static EnTransducer _Transducer = new EnTransducer();
	private static EnDEPSContext _DEPSContext = new EnDEPSContext();
	private static EnDEPSContextInverse _DEPSContextInverse = new EnDEPSContextInverse();

	public static EnDEPSContext get_DEPSContext() {
		if (_DEPSContext.get_cdb() == null)
			loadDEPSContext();
		return _DEPSContext;
	}

	public static EnDEPSContextInverse get_DEPSContextInverse() {
		if (_DEPSContextInverse.get_cdb() == null)
			loadDEPSContextInverse();
		return _DEPSContextInverse;
	}

	public static EnGlove get_glove() {
		if (_glove.get_cdb() == null)
			loadGlove();
		return _glove;
	}

	public static void loadGlove() {
		_glove.init(_dimGlove);
	}

	public static EnMikolov get_mikolov() {
		if (_mikolov.get_cdb() == null)
			loadMikolov();
		return _mikolov;
	}

	public static EnTurian get_turian() {
		if (_turian.get_cdb() == null)
			loadTurian();
		return _turian;
	}

	public static EnDIRT get_DIRT() {
		if (_DIRT.get_cdb() == null)
			loadDIRT();
		return _DIRT;
	}

	public static EnGeo get_GEO() {
		if (_GEO.get_cdb() == null)
			loadGEO();
		return _GEO;
	}

	public static EnTransducer get_Transducer() {
		if (_Transducer.get_cdb() == null)
			loadTransducer();
		return _Transducer;
	}

	public static EnDEPS get_DEPS() {
		if (_DEPS.get_cdb() == null)
			loadDEPS();
		return _DEPS;
	}

	public static void loadDIRT() {
		_DIRT.init();
	}

	public static void loadMikolov() {
		_mikolov.init(_dimMikolov);
	}

	public static void loadTurian() {
		_turian.init(_dimTurian);
	}

	public static void loadDEPS() {
		_DEPS.init(_dimDEPS);
	}

	public static void loadGEO() {
		_GEO.init();
	}


	public static void loadTransducer() {
		_Transducer.init();
	}

	public static void loadDEPSContext() {
		_DEPSContext.init(_dimDEPS);
	}

	public static void loadDEPSContextInverse() {
		_DEPSContextInverse.init(_dimDEPS);
	}
}
