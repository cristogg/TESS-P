package b4a.tessp;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class starter extends  android.app.Service{
	public static class starter_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (starter) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, starter.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, true, BA.class);
		}

	}
    static starter mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return starter.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "b4a.tessp", "b4a.tessp.starter");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.tessp.starter", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!true && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, false) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (starter) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (true) {
			ServiceHelper.StarterHelper.runWaitForLayouts();
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (starter) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (true)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (starter) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (true) {
            BA.LogInfo("** Service (starter) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (starter) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static String _service_uuid = "";
public static String _characteristic_uuid_rx = "";
public static String _characteristic_uuid_tx = "";
public static boolean _gps_permiso_ok = false;
public static anywheresoftware.b4a.objects.BleManager2 _ble = null;
public static boolean _ble_permiso_ok = false;
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static boolean _estadocbsabe = false;
public static int _udprx = 0;
public static int _nwr = 0;
public static int _contadorr = 0;
public static int _cuenta = 0;
public static int _cuentaudp = 0;
public static int _cuentasend = 0;
public static int _cuentasg = 0;
public static int _tiemposalvarsg = 0;
public static anywheresoftware.b4a.objects.Timer _tim1sserv = null;
public static anywheresoftware.b4a.randomaccessfile.RandomAccessFile _raf = null;
public static String _lat = "";
public static String _lon = "";
public static float _alt = 0f;
public static boolean _datoble = false;
public static String _idmac = "";
public static float _tamb = 0f;
public static float _tobj = 0f;
public static float _frecuencia = 0f;
public static float _magnitud = 0f;
public static float _altura = 0f;
public static float _azimut = 0f;
public static float _alturaacel = 0f;
public static float _azimutacel = 0f;
public static float _tambm = 0f;
public static float _tobjm = 0f;
public static float _frecuenciam = 0f;
public static float _magnitudm = 0f;
public static float _alturam = 0f;
public static float _azimutm = 0f;
public static anywheresoftware.b4a.objects.collections.Map _map1 = null;
public static anywheresoftware.b4a.objects.collections.Map _mapbt = null;
public static String _msg = "";
public static anywheresoftware.b4a.randomaccessfile.RandomAccessFile _tsp_cfg = null;
public static anywheresoftware.b4a.randomaccessfile.RandomAccessFile _rawrx = null;
public static String _namesensor = "";
public static String _namesensorrx = "";
public static float _zpoint = 0f;
public static int _espera = 0;
public static int _esperamsg = 0;
public static int _esperasalvar = 0;
public static anywheresoftware.b4a.gps.GPS _gps1 = null;
public static String _rxbybt = "";
public static String _dirurl = "";
public static String _dirurlselec = "";
public static boolean _mytess = false;
public static boolean _otrosbt = false;
public static boolean _otrosudp = false;
public static String _lugar = "";
public static boolean _ficherocreado = false;
public static boolean _ficherocreadotodo = false;
public static String _nombrefich = "";
public static String _namefile = "";
public static String _nombrefichtodo = "";
public static String _namesensorrxotro = "";
public static int _tescaneo = 0;
public static anywheresoftware.b4a.audio.Beeper _bep = null;
public static anywheresoftware.b4a.audio.Beeper _bepend = null;
public static int _contabt = 0;
public static int _contarxscan = 0;
public static float _vbat = 0f;
public static int _sequdp = 0;
public static int _sequdpold = 0;
public static int _udplost = 0;
public static boolean _flagalarmir = false;
public static boolean _flagalarmmv = false;
public static boolean _alarmir = false;
public static boolean _alarmmv = false;
public static int _periodoalarmaacustica = 0;
public static float _refalarmir = 0f;
public static float _refalarmmv = 0f;
public static anywheresoftware.b4a.objects.NotificationWrapper _notification = null;
public static String _inform = "";
public static anywheresoftware.b4a.phone.Phone.PhoneWakeState _pws = null;
public static boolean _salvandoscan = false;
public static boolean _cbsalvar = false;
public static int _nlinea = 0;
public static int _secuencia = 0;
public static int _rxcount = 0;
public static boolean _arrancado = false;
public static int _esperapelin = 0;
public static anywheresoftware.b4a.objects.collections.List _connectedservices = null;
public static String _connectedname = "";
public static anywheresoftware.b4a.objects.BleManager2 _manager = null;
public static String _currentstatetext = "";
public static int _currentstate = 0;
public static int _btrssi = 0;
public static boolean _connected = false;
public static float _deltair = 0f;
public static float _deltamag = 0f;
public static int _segscan = 0;
public static int _es155espera = 0;
public b4a.tessp.main _main = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 215;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 216;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 217;BA.debugLine="End Sub";
return false;
}
public static anywheresoftware.b4a.objects.PanelWrapper  _createcharacteristicitemstar(String _id,byte[] _data) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _p = null;
String _nserie = "";
 //BA.debugLineNum = 335;BA.debugLine="Sub CreateCharacteristicItemStar(Id As String, Dat";
 //BA.debugLineNum = 336;BA.debugLine="Dim p As JSONParser";
_p = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 337;BA.debugLine="Dim nserie As String";
_nserie = "";
 //BA.debugLineNum = 339;BA.debugLine="p.Initialize(BytesToString(Data, 0, Data.Length,";
_p.Initialize(anywheresoftware.b4a.keywords.Common.BytesToString(_data,(int) (0),_data.length,"UTF8"));
 //BA.debugLineNum = 341;BA.debugLine="Try";
try { //BA.debugLineNum = 342;BA.debugLine="Map1 = p.NextObject";
_map1 = _p.NextObject();
 //BA.debugLineNum = 343;BA.debugLine="secuencia = Map1.Get(\"seq\")";
_secuencia = (int)(BA.ObjectToNumber(_map1.Get((Object)("seq"))));
 //BA.debugLineNum = 346;BA.debugLine="TObj = Map1.Get(\"tsky\")";
_tobj = (float)(BA.ObjectToNumber(_map1.Get((Object)("tsky"))));
 //BA.debugLineNum = 347;BA.debugLine="TAmb = Map1.Get(\"tamb\")";
_tamb = (float)(BA.ObjectToNumber(_map1.Get((Object)("tamb"))));
 //BA.debugLineNum = 348;BA.debugLine="Magnitud =Map1.Get(\"mag\")";
_magnitud = (float)(BA.ObjectToNumber(_map1.Get((Object)("mag"))));
 //BA.debugLineNum = 349;BA.debugLine="Frecuencia = Map1.Get(\"freq\")";
_frecuencia = (float)(BA.ObjectToNumber(_map1.Get((Object)("freq"))));
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 351;BA.debugLine="Log(\"nastic\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63407888","nastic",0);
 //BA.debugLineNum = 352;BA.debugLine="inform = (\" Error cadena json.\")";
_inform = (" Error cadena json.");
 };
 //BA.debugLineNum = 355;BA.debugLine="Try";
try { //BA.debugLineNum = 356;BA.debugLine="Vbat = Map1.Get(\"vbat\")";
_vbat = (float)(BA.ObjectToNumber(_map1.Get((Object)("vbat"))));
 } 
       catch (Exception e18) {
			processBA.setLastException(e18); //BA.debugLineNum = 358;BA.debugLine="Vbat = 500";
_vbat = (float) (500);
 };
 //BA.debugLineNum = 362;BA.debugLine="Try";
try { //BA.debugLineNum = 363;BA.debugLine="Altura = Map1.Get(\"alt\")";
_altura = (float)(BA.ObjectToNumber(_map1.Get((Object)("alt"))));
 //BA.debugLineNum = 364;BA.debugLine="Azimut = Map1.Get(\"azi\")";
_azimut = (float)(BA.ObjectToNumber(_map1.Get((Object)("azi"))));
 } 
       catch (Exception e24) {
			processBA.setLastException(e24); //BA.debugLineNum = 366;BA.debugLine="Altura = 0";
_altura = (float) (0);
 //BA.debugLineNum = 367;BA.debugLine="Azimut = 0";
_azimut = (float) (0);
 };
 //BA.debugLineNum = 371;BA.debugLine="Try";
try { //BA.debugLineNum = 372;BA.debugLine="AlturaAcel = Map1.Get(\"ala\")";
_alturaacel = (float)(BA.ObjectToNumber(_map1.Get((Object)("ala"))));
 //BA.debugLineNum = 373;BA.debugLine="AzimutAcel = Map1.Get(\"azm\")";
_azimutacel = (float)(BA.ObjectToNumber(_map1.Get((Object)("azm"))));
 } 
       catch (Exception e31) {
			processBA.setLastException(e31); //BA.debugLineNum = 375;BA.debugLine="AlturaAcel = 0";
_alturaacel = (float) (0);
 //BA.debugLineNum = 376;BA.debugLine="AzimutAcel = 0";
_azimutacel = (float) (0);
 };
 //BA.debugLineNum = 381;BA.debugLine="If secuencia = 0 Then  'En los barridos TAS empie";
if (_secuencia==0) { 
 //BA.debugLineNum = 382;BA.debugLine="nWr = 0";
_nwr = (int) (0);
 //BA.debugLineNum = 383;BA.debugLine="salvandoScan = True";
_salvandoscan = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 384;BA.debugLine="NameFile =  \"AS_\" & DateTime.Date(DateTime.Now)";
_namefile = "AS_"+anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+"_"+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 386;BA.debugLine="GuardaDato";
_guardadato();
 //BA.debugLineNum = 387;BA.debugLine="bep.Initialize(400,800)";
_bep.Initialize((int) (400),(int) (800));
 //BA.debugLineNum = 388;BA.debugLine="bep.Beep";
_bep.Beep();
 }else if(_secuencia<145 && _salvandoscan==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 390;BA.debugLine="Es155espera = 0";
_es155espera = (int) (0);
 //BA.debugLineNum = 391;BA.debugLine="Main.scaner = True";
mostCurrent._main._scaner /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 392;BA.debugLine="GuardaDato";
_guardadato();
 //BA.debugLineNum = 393;BA.debugLine="If secuencia = 144 Then";
if (_secuencia==144) { 
 //BA.debugLineNum = 394;BA.debugLine="salvandoScan = False";
_salvandoscan = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 395;BA.debugLine="Log(\"fin barrido\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63407932","fin barrido",0);
 //BA.debugLineNum = 396;BA.debugLine="bep.Initialize(400,500)";
_bep.Initialize((int) (400),(int) (500));
 //BA.debugLineNum = 397;BA.debugLine="bep.Beep";
_bep.Beep();
 };
 }else if(_secuencia==200) { 
 //BA.debugLineNum = 400;BA.debugLine="salvandoScan = False";
_salvandoscan = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 401;BA.debugLine="Log(\"Fin scan\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63407938","Fin scan",0);
 //BA.debugLineNum = 402;BA.debugLine="Es155espera = 0";
_es155espera = (int) (0);
 }else if(_secuencia==155) { 
 //BA.debugLineNum = 404;BA.debugLine="Es155espera = Es155espera + 1";
_es155espera = (int) (_es155espera+1);
 }else {
 //BA.debugLineNum = 407;BA.debugLine="Es155espera = 0";
_es155espera = (int) (0);
 //BA.debugLineNum = 408;BA.debugLine="Main.scaner = False";
mostCurrent._main._scaner /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 418;BA.debugLine="If (EstadoCBsabe = True)  Then";
if ((_estadocbsabe==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 419;BA.debugLine="ContadorR =  ContadorR + 1";
_contadorr = (int) (_contadorr+1);
 //BA.debugLineNum = 420;BA.debugLine="MagnitudM = MagnitudM + Magnitud";
_magnitudm = (float) (_magnitudm+_magnitud);
 //BA.debugLineNum = 421;BA.debugLine="FrecuenciaM = FrecuenciaM + Frecuencia";
_frecuenciam = (float) (_frecuenciam+_frecuencia);
 //BA.debugLineNum = 422;BA.debugLine="TAmbM = TAmbM + TAmb";
_tambm = (float) (_tambm+_tamb);
 //BA.debugLineNum = 423;BA.debugLine="TObjM = TObjM + TObj";
_tobjm = (float) (_tobjm+_tobj);
 }else {
 //BA.debugLineNum = 425;BA.debugLine="ContadorR = 1";
_contadorr = (int) (1);
 //BA.debugLineNum = 426;BA.debugLine="MagnitudM =  Magnitud";
_magnitudm = _magnitud;
 //BA.debugLineNum = 427;BA.debugLine="TAmbM =  TAmb";
_tambm = _tamb;
 //BA.debugLineNum = 428;BA.debugLine="TObjM =  TObj";
_tobjm = _tobj;
 };
 //BA.debugLineNum = 432;BA.debugLine="DatoBle = True";
_datoble = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 433;BA.debugLine="Cuenta = Cuenta + 1  'Cuenta total de rx BT";
_cuenta = (int) (_cuenta+1);
 //BA.debugLineNum = 434;BA.debugLine="EsperaMsg= 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 435;BA.debugLine="End Sub";
return null;
}
public static anywheresoftware.b4a.objects.NotificationWrapper  _createnotification(String _body) throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub CreateNotification (Body As String) As Notific";
 //BA.debugLineNum = 177;BA.debugLine="notification.Initialize2(notification.IMPORTANCE_";
_notification.Initialize2(_notification.IMPORTANCE_LOW);
 //BA.debugLineNum = 178;BA.debugLine="notification.Icon = \"icon.png\"";
_notification.setIcon("icon.png");
 //BA.debugLineNum = 183;BA.debugLine="If (EsperaMsg >10) Then";
if ((_esperamsg>10)) { 
 //BA.debugLineNum = 184;BA.debugLine="inform = \"NO sensor found.\"";
_inform = "NO sensor found.";
 };
 //BA.debugLineNum = 187;BA.debugLine="notification.SetInfo(\"TESS P\", inform, Main)";
_notification.SetInfoNew(processBA,BA.ObjectToCharSequence("TESS P"),BA.ObjectToCharSequence(_inform),(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 189;BA.debugLine="Return notification";
if (true) return _notification;
 //BA.debugLineNum = 190;BA.debugLine="End Sub";
return null;
}
public static String  _disconnect() throws Exception{
 //BA.debugLineNum = 247;BA.debugLine="Public Sub Disconnect";
 //BA.debugLineNum = 248;BA.debugLine="manager.Disconnect";
_manager.Disconnect();
 //BA.debugLineNum = 249;BA.debugLine="Manager_Disconnected";
_manager_disconnected();
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
public static String  _gps_locationchanged(anywheresoftware.b4a.gps.LocationWrapper _location1) throws Exception{
 //BA.debugLineNum = 204;BA.debugLine="Sub GPS_LocationChanged (Location1 As Location)";
 //BA.debugLineNum = 205;BA.debugLine="Lat =  Location1.ConvertToSeconds(Location1.Latit";
_lat = _location1.ConvertToSeconds(_location1.getLatitude());
 //BA.debugLineNum = 206;BA.debugLine="Lon =  Location1.ConvertToSeconds(Location1.Longi";
_lon = _location1.ConvertToSeconds(_location1.getLongitude());
 //BA.debugLineNum = 208;BA.debugLine="Alt =  Location1.Altitude";
_alt = (float) (_location1.getAltitude());
 //BA.debugLineNum = 210;BA.debugLine="End Sub";
return "";
}
public static String  _guardadato() throws Exception{
String _nombre = "";
String _comienzo = "";
 //BA.debugLineNum = 472;BA.debugLine="Sub GuardaDato			' Guarda Datos";
 //BA.debugLineNum = 473;BA.debugLine="Dim nombre As String";
_nombre = "";
 //BA.debugLineNum = 474;BA.debugLine="Dim comienzo As String";
_comienzo = "";
 //BA.debugLineNum = 476;BA.debugLine="nombre = NameSensor & \"_\" & NameFile & \"_\" & Luga";
_nombre = _namesensor+"_"+_namefile+"_"+_lugar+".txt";
 //BA.debugLineNum = 477;BA.debugLine="nombreFich = nombre";
_nombrefich = _nombre;
 //BA.debugLineNum = 480;BA.debugLine="If File.Exists(rp.GetSafeDirDefaultExternal(\"\"),";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rp.GetSafeDirDefaultExternal(""),_nombrefich)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 481;BA.debugLine="GuardaTrack";
_guardatrack();
 }else {
 //BA.debugLineNum = 483;BA.debugLine="RxCount = 0";
_rxcount = (int) (0);
 //BA.debugLineNum = 484;BA.debugLine="comienzo = \"\"	'Para completar lista de variables";
_comienzo = "";
 //BA.debugLineNum = 485;BA.debugLine="comienzo = \"# \" & TAB & NameSensor & TAB & ZPoin";
_comienzo = "# "+anywheresoftware.b4a.keywords.Common.TAB+_namesensor+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(_zpoint)+anywheresoftware.b4a.keywords.Common.TAB+"T IR"+anywheresoftware.b4a.keywords.Common.TAB+"T Sens"+anywheresoftware.b4a.keywords.Common.TAB+"Mag  "+anywheresoftware.b4a.keywords.Common.TAB+"Hz   "+anywheresoftware.b4a.keywords.Common.TAB;
 //BA.debugLineNum = 486;BA.debugLine="comienzo = comienzo & \"Alt\" & TAB & \"Azi  \" & TA";
_comienzo = _comienzo+"Alt"+anywheresoftware.b4a.keywords.Common.TAB+"Azi  "+anywheresoftware.b4a.keywords.Common.TAB+"Lat    "+anywheresoftware.b4a.keywords.Common.TAB+"Lon   "+anywheresoftware.b4a.keywords.Common.TAB+"SL m"+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 487;BA.debugLine="Try";
try { //BA.debugLineNum = 488;BA.debugLine="File.WriteString(rp.GetSafeDirDefaultExternal(\"";
anywheresoftware.b4a.keywords.Common.File.WriteString(_rp.GetSafeDirDefaultExternal(""),_nombrefich,_comienzo);
 } 
       catch (Exception e15) {
			processBA.setLastException(e15); //BA.debugLineNum = 490;BA.debugLine="ToastMessageShow(\"Can't write file.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Can't write file."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 491;BA.debugLine="Log(\"Can't write file.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63604499","Can't write file.",0);
 };
 //BA.debugLineNum = 494;BA.debugLine="FicheroCreado = True";
_ficherocreado = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 496;BA.debugLine="GuardaTrack";
_guardatrack();
 };
 //BA.debugLineNum = 501;BA.debugLine="End Sub";
return "";
}
public static String  _guardatrack() throws Exception{
String _pp = "";
String _fecha = "";
 //BA.debugLineNum = 507;BA.debugLine="Sub GuardaTrack () ' Guarda una linea";
 //BA.debugLineNum = 508;BA.debugLine="Dim pp As String";
_pp = "";
 //BA.debugLineNum = 509;BA.debugLine="Dim fecha As String";
_fecha = "";
 //BA.debugLineNum = 511;BA.debugLine="If CBsalvar = True Then  'salvar lecturas sueltas";
if (_cbsalvar==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 512;BA.debugLine="nlinea = nlinea + 1";
_nlinea = (int) (_nlinea+1);
 }else {
 //BA.debugLineNum = 514;BA.debugLine="nlinea = secuencia + 1";
_nlinea = (int) (_secuencia+1);
 };
 //BA.debugLineNum = 518;BA.debugLine="fecha = nlinea &\" \" & TAB & DateTime.Date(DateTim";
_fecha = BA.NumberToString(_nlinea)+" "+anywheresoftware.b4a.keywords.Common.TAB+anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" "+anywheresoftware.b4a.keywords.Common.TAB+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 519;BA.debugLine="If nlinea > 200 Then  ' estamos en lecturas suelt";
if (_nlinea>200) { 
 //BA.debugLineNum = 520;BA.debugLine="pp =  fecha & TAB & Round2(TObjM/ContadorR , 2)";
_pp = _fecha+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tobjm/(double)_contadorr,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tambm/(double)_contadorr,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_magnitudm/(double)_contadorr,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB;
 //BA.debugLineNum = 521;BA.debugLine="If Frecuencia < 100 Then";
if (_frecuencia<100) { 
 //BA.debugLineNum = 523;BA.debugLine="pp = pp & Round2(FrecuenciaM/ContadorR,3)& TAB";
_pp = _pp+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_frecuenciam/(double)_contadorr,(int) (3)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_altura,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_azimut,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB;
 }else {
 //BA.debugLineNum = 526;BA.debugLine="pp = pp & Round2(FrecuenciaM/ContadorR,0)& TAB";
_pp = _pp+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_frecuenciam/(double)_contadorr,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_altura,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_azimut,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB;
 };
 }else {
 //BA.debugLineNum = 529;BA.debugLine="pp =  fecha & TAB & Round2(TObj , 2) &TAB & Roun";
_pp = _fecha+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tobj,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tamb,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_magnitud,(int) (2)))+anywheresoftware.b4a.keywords.Common.TAB;
 //BA.debugLineNum = 530;BA.debugLine="If Frecuencia < 100 Then";
if (_frecuencia<100) { 
 //BA.debugLineNum = 531;BA.debugLine="pp = pp & Round2(Frecuencia,3)& TAB  & Round2(A";
_pp = _pp+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_frecuencia,(int) (3)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_altura,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_azimut,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB;
 }else {
 //BA.debugLineNum = 533;BA.debugLine="pp = pp & Round2(Frecuencia,0)& TAB  & Round2(A";
_pp = _pp+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_frecuencia,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_altura,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_azimut,(int) (0)))+anywheresoftware.b4a.keywords.Common.TAB;
 };
 };
 //BA.debugLineNum = 537;BA.debugLine="If GPS1.IsInitialized Then";
if (_gps1.IsInitialized()) { 
 //BA.debugLineNum = 538;BA.debugLine="pp = pp & Lat & TAB  & Lon & TAB &  Round2(Alt,0";
_pp = _pp+_lat+anywheresoftware.b4a.keywords.Common.TAB+_lon+anywheresoftware.b4a.keywords.Common.TAB+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_alt,(int) (0)))+anywheresoftware.b4a.keywords.Common.CRLF;
 };
 //BA.debugLineNum = 540;BA.debugLine="Log(pp)";
anywheresoftware.b4a.keywords.Common.LogImpl("63670049",_pp,0);
 //BA.debugLineNum = 541;BA.debugLine="Try";
try { //BA.debugLineNum = 542;BA.debugLine="raf.Initialize(rp.GetSafeDirDefaultExternal(\"\"),";
_raf.Initialize(_rp.GetSafeDirDefaultExternal(""),_nombrefich,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 543;BA.debugLine="raf.WriteBytes(pp.GetBytes(\"Windows-1252\"), 0, p";
_raf.WriteBytes(_pp.getBytes("Windows-1252"),(int) (0),_pp.length(),_raf.getSize());
 } 
       catch (Exception e32) {
			processBA.setLastException(e32); //BA.debugLineNum = 545;BA.debugLine="ToastMessageShow(\"Can't write file.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Can't write file."),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 547;BA.debugLine="nWr = nWr + 1";
_nwr = (int) (_nwr+1);
 //BA.debugLineNum = 548;BA.debugLine="raf.Close";
_raf.Close();
 //BA.debugLineNum = 550;BA.debugLine="End Sub";
return "";
}
public static String  _manager_connected(anywheresoftware.b4a.objects.collections.List _services) throws Exception{
 //BA.debugLineNum = 447;BA.debugLine="Sub Manager_Connected (services As List)";
 //BA.debugLineNum = 449;BA.debugLine="inform = esperapelin & \" BT Connected\"";
_inform = BA.NumberToString(_esperapelin)+" BT Connected";
 //BA.debugLineNum = 450;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63538947",_inform,0);
 //BA.debugLineNum = 452;BA.debugLine="ConnectedServices = services";
_connectedservices = _services;
 //BA.debugLineNum = 455;BA.debugLine="If manager.RequestMtu(200) Then";
if (_manager.RequestMtu((int) (200))) { 
 //BA.debugLineNum = 456;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 458;BA.debugLine="Log(\" Connected \" & \"mtu ok\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63538955"," Connected "+"mtu ok",0);
 }else {
 //BA.debugLineNum = 461;BA.debugLine="Log( \" Connected \" & \"mtu fail\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63538958"," Connected "+"mtu fail",0);
 };
 //BA.debugLineNum = 463;BA.debugLine="CallSub(Main, \"StateChanged\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._main.getObject()),"StateChanged");
 //BA.debugLineNum = 465;BA.debugLine="End Sub";
return "";
}
public static String  _manager_dataavailable(String _serviceid,anywheresoftware.b4a.objects.collections.Map _characteristics) throws Exception{
String _id = "";
 //BA.debugLineNum = 318;BA.debugLine="Sub Manager_DataAvailable (ServiceId As String, Ch";
 //BA.debugLineNum = 319;BA.debugLine="RxCount = RxCount+1";
_rxcount = (int) (_rxcount+1);
 //BA.debugLineNum = 321;BA.debugLine="CallSub3(Main, \"DataAvailable\", ServiceId, Charac";
anywheresoftware.b4a.keywords.Common.CallSubNew3(processBA,(Object)(mostCurrent._main.getObject()),"DataAvailable",(Object)(_serviceid),(Object)(_characteristics));
 //BA.debugLineNum = 323;BA.debugLine="For Each id As String In Characteristics.Keys";
{
final anywheresoftware.b4a.BA.IterableList group3 = _characteristics.Keys();
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_id = BA.ObjectToString(group3.Get(index3));
 //BA.debugLineNum = 324;BA.debugLine="If id = CHARACTERISTIC_UUID_RX Then";
if ((_id).equals(_characteristic_uuid_rx)) { 
 //BA.debugLineNum = 326;BA.debugLine="CreateCharacteristicItemStar(id, Characteristic";
_createcharacteristicitemstar(_id,(byte[])(_characteristics.Get((Object)(_id))));
 };
 }
};
 //BA.debugLineNum = 330;BA.debugLine="End Sub";
return "";
}
public static String  _manager_devicefound(String _name,String _id,anywheresoftware.b4a.objects.collections.Map _advertisingdata,double _rssi) throws Exception{
 //BA.debugLineNum = 270;BA.debugLine="Sub Manager_DeviceFound (Name As String, Id As Str";
 //BA.debugLineNum = 274;BA.debugLine="IdMac = Id";
_idmac = _id;
 //BA.debugLineNum = 279;BA.debugLine="Try";
try { //BA.debugLineNum = 280;BA.debugLine="If Name.Contains(NameSensor.SubString2(0,6)) The";
if (_name.contains(_namesensor.substring((int) (0),(int) (6)))) { 
 //BA.debugLineNum = 281;BA.debugLine="BTrssi = RSSI";
_btrssi = (int) (_rssi);
 //BA.debugLineNum = 282;BA.debugLine="inform = \"Found \" & Name";
_inform = "Found "+_name;
 //BA.debugLineNum = 283;BA.debugLine="Log (inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63211277",_inform,0);
 //BA.debugLineNum = 284;BA.debugLine="ConnectedName = Name";
_connectedname = _name;
 //BA.debugLineNum = 285;BA.debugLine="manager.StopScan";
_manager.StopScan();
 //BA.debugLineNum = 288;BA.debugLine="manager.Connect2(Id, True) 'disabling auto conn";
_manager.Connect2(_id,anywheresoftware.b4a.keywords.Common.True);
 };
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 291;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("63211285",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
 //BA.debugLineNum = 295;BA.debugLine="End Sub";
return "";
}
public static String  _manager_disconnected() throws Exception{
 //BA.debugLineNum = 440;BA.debugLine="Sub Manager_Disconnected";
 //BA.debugLineNum = 441;BA.debugLine="Log(\"Disconnected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63473409","Disconnected",0);
 //BA.debugLineNum = 442;BA.debugLine="inform = inform & \" BT Disconnected\"";
_inform = _inform+" BT Disconnected";
 //BA.debugLineNum = 443;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 444;BA.debugLine="CallSub(Main, \"StateChanged\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._main.getObject()),"StateChanged");
 //BA.debugLineNum = 445;BA.debugLine="End Sub";
return "";
}
public static String  _manager_statechanged(int _state) throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub Manager_StateChanged (State As Int)";
 //BA.debugLineNum = 255;BA.debugLine="Select State";
switch (BA.switchObjectToInt(_state,_manager.STATE_POWERED_OFF,_manager.STATE_POWERED_ON,_manager.STATE_UNSUPPORTED)) {
case 0: {
 //BA.debugLineNum = 257;BA.debugLine="currentStateText = \"POWERED OFF\"";
_currentstatetext = "POWERED OFF";
 break; }
case 1: {
 //BA.debugLineNum = 259;BA.debugLine="currentStateText = \"POWERED ON\"";
_currentstatetext = "POWERED ON";
 break; }
case 2: {
 //BA.debugLineNum = 261;BA.debugLine="currentStateText = \"UNSUPPORTED\"";
_currentstatetext = "UNSUPPORTED";
 break; }
}
;
 //BA.debugLineNum = 263;BA.debugLine="currentState = State";
_currentstate = _state;
 //BA.debugLineNum = 264;BA.debugLine="inform = \"BT \" & currentStateText";
_inform = "BT "+_currentstatetext;
 //BA.debugLineNum = 266;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Public SERVICE_UUID  As String = \"6e400001-b5a3-f";
_service_uuid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
 //BA.debugLineNum = 10;BA.debugLine="Public CHARACTERISTIC_UUID_RX  As String = \"6e400";
_characteristic_uuid_rx = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
 //BA.debugLineNum = 11;BA.debugLine="Public CHARACTERISTIC_UUID_TX  As String = \"6e400";
_characteristic_uuid_tx = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
 //BA.debugLineNum = 13;BA.debugLine="Public gps_permiso_ok As Boolean";
_gps_permiso_ok = false;
 //BA.debugLineNum = 14;BA.debugLine="Dim ble As BleManager2";
_ble = new anywheresoftware.b4a.objects.BleManager2();
 //BA.debugLineNum = 15;BA.debugLine="Public ble_permiso_ok As Boolean";
_ble_permiso_ok = false;
 //BA.debugLineNum = 17;BA.debugLine="Public rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 19;BA.debugLine="Dim EstadoCBsabe As Boolean";
_estadocbsabe = false;
 //BA.debugLineNum = 20;BA.debugLine="Dim udpRx As Int";
_udprx = 0;
 //BA.debugLineNum = 21;BA.debugLine="Dim nWr As Int";
_nwr = 0;
 //BA.debugLineNum = 22;BA.debugLine="Dim ContadorR As Int";
_contadorr = 0;
 //BA.debugLineNum = 23;BA.debugLine="Dim Cuenta As Int";
_cuenta = 0;
 //BA.debugLineNum = 24;BA.debugLine="Dim CuentaUDP As Int";
_cuentaudp = 0;
 //BA.debugLineNum = 25;BA.debugLine="Dim CuentaSend As Int";
_cuentasend = 0;
 //BA.debugLineNum = 26;BA.debugLine="Dim CuentaSg As Int";
_cuentasg = 0;
 //BA.debugLineNum = 27;BA.debugLine="Dim TiempoSalvarSg As Int";
_tiemposalvarsg = 0;
 //BA.debugLineNum = 29;BA.debugLine="Dim Tim1sServ As Timer";
_tim1sserv = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 31;BA.debugLine="Dim raf As RandomAccessFile";
_raf = new anywheresoftware.b4a.randomaccessfile.RandomAccessFile();
 //BA.debugLineNum = 33;BA.debugLine="Dim Lat As String";
_lat = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim Lon As String";
_lon = "";
 //BA.debugLineNum = 35;BA.debugLine="Dim Alt As Float 'String";
_alt = 0f;
 //BA.debugLineNum = 36;BA.debugLine="Dim DatoBle As Boolean";
_datoble = false;
 //BA.debugLineNum = 37;BA.debugLine="Dim IdMac As String";
_idmac = "";
 //BA.debugLineNum = 40;BA.debugLine="Dim TAmb, TObj, Frecuencia, Magnitud, Altura, Azi";
_tamb = 0f;
_tobj = 0f;
_frecuencia = 0f;
_magnitud = 0f;
_altura = 0f;
_azimut = 0f;
 //BA.debugLineNum = 41;BA.debugLine="Dim AlturaAcel, AzimutAcel As Float";
_alturaacel = 0f;
_azimutacel = 0f;
 //BA.debugLineNum = 42;BA.debugLine="Dim TAmbM, TObjM, FrecuenciaM, MagnitudM, AlturaM";
_tambm = 0f;
_tobjm = 0f;
_frecuenciam = 0f;
_magnitudm = 0f;
_alturam = 0f;
_azimutm = 0f;
 //BA.debugLineNum = 44;BA.debugLine="Dim Map1 As Map";
_map1 = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 45;BA.debugLine="Dim MapBT As Map";
_mapbt = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 46;BA.debugLine="Dim msg As String";
_msg = "";
 //BA.debugLineNum = 48;BA.debugLine="Dim tsp_cfg As RandomAccessFile";
_tsp_cfg = new anywheresoftware.b4a.randomaccessfile.RandomAccessFile();
 //BA.debugLineNum = 49;BA.debugLine="Dim rawrx As RandomAccessFile";
_rawrx = new anywheresoftware.b4a.randomaccessfile.RandomAccessFile();
 //BA.debugLineNum = 50;BA.debugLine="Dim NameSensor As String";
_namesensor = "";
 //BA.debugLineNum = 51;BA.debugLine="Dim NameSensorRx As String";
_namesensorrx = "";
 //BA.debugLineNum = 52;BA.debugLine="Dim ZPoint As Float";
_zpoint = 0f;
 //BA.debugLineNum = 53;BA.debugLine="Dim espera As Int";
_espera = 0;
 //BA.debugLineNum = 55;BA.debugLine="Dim EsperaMsg As Int";
_esperamsg = 0;
 //BA.debugLineNum = 56;BA.debugLine="Dim EsperaSalvar As Int";
_esperasalvar = 0;
 //BA.debugLineNum = 59;BA.debugLine="Dim GPS1 As GPS";
_gps1 = new anywheresoftware.b4a.gps.GPS();
 //BA.debugLineNum = 60;BA.debugLine="Dim RxByBT As String";
_rxbybt = "";
 //BA.debugLineNum = 63;BA.debugLine="Dim dirurl As String";
_dirurl = "";
 //BA.debugLineNum = 64;BA.debugLine="Dim dirurlSelec As String";
_dirurlselec = "";
 //BA.debugLineNum = 65;BA.debugLine="Dim MyTess As Boolean";
_mytess = false;
 //BA.debugLineNum = 66;BA.debugLine="Dim OtrosBT As Boolean";
_otrosbt = false;
 //BA.debugLineNum = 67;BA.debugLine="Dim OtrosUdp As Boolean";
_otrosudp = false;
 //BA.debugLineNum = 69;BA.debugLine="Dim Lugar As String = \"site\"";
_lugar = "site";
 //BA.debugLineNum = 71;BA.debugLine="Dim FicheroCreado As Boolean";
_ficherocreado = false;
 //BA.debugLineNum = 72;BA.debugLine="Dim FicheroCreadoTodo As Boolean";
_ficherocreadotodo = false;
 //BA.debugLineNum = 73;BA.debugLine="Dim nombreFich As String = \"hola.txt\"";
_nombrefich = "hola.txt";
 //BA.debugLineNum = 74;BA.debugLine="Dim NameFile As String";
_namefile = "";
 //BA.debugLineNum = 75;BA.debugLine="Dim nombreFichTodo As String";
_nombrefichtodo = "";
 //BA.debugLineNum = 76;BA.debugLine="Dim NameSensorRxOtro As String";
_namesensorrxotro = "";
 //BA.debugLineNum = 78;BA.debugLine="Dim Tescaneo As Int";
_tescaneo = 0;
 //BA.debugLineNum = 79;BA.debugLine="Dim bep As Beeper";
_bep = new anywheresoftware.b4a.audio.Beeper();
 //BA.debugLineNum = 80;BA.debugLine="Dim bepEnd As Beeper";
_bepend = new anywheresoftware.b4a.audio.Beeper();
 //BA.debugLineNum = 81;BA.debugLine="Dim	ContaBT As Int";
_contabt = 0;
 //BA.debugLineNum = 82;BA.debugLine="Dim	ContaRxScan As Int";
_contarxscan = 0;
 //BA.debugLineNum = 83;BA.debugLine="Dim	Vbat As Float";
_vbat = 0f;
 //BA.debugLineNum = 85;BA.debugLine="Dim	SeqUdp As Int";
_sequdp = 0;
 //BA.debugLineNum = 86;BA.debugLine="Dim	SeqUdpOld As Int";
_sequdpold = 0;
 //BA.debugLineNum = 87;BA.debugLine="Dim	UdpLost As Int";
_udplost = 0;
 //BA.debugLineNum = 89;BA.debugLine="Dim FlagAlarmIR As Boolean";
_flagalarmir = false;
 //BA.debugLineNum = 90;BA.debugLine="Dim FlagAlarmMV As Boolean";
_flagalarmmv = false;
 //BA.debugLineNum = 91;BA.debugLine="Dim AlarmIR As Boolean";
_alarmir = false;
 //BA.debugLineNum = 92;BA.debugLine="Dim AlarmMV As Boolean";
_alarmmv = false;
 //BA.debugLineNum = 93;BA.debugLine="Dim PeriodoAlarmaAcustica As Int";
_periodoalarmaacustica = 0;
 //BA.debugLineNum = 95;BA.debugLine="Dim RefAlarmIR As Float";
_refalarmir = 0f;
 //BA.debugLineNum = 96;BA.debugLine="Dim RefAlarmMV As Float";
_refalarmmv = 0f;
 //BA.debugLineNum = 99;BA.debugLine="Dim notification As Notification";
_notification = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 100;BA.debugLine="Dim inform As String = \"Init\"";
_inform = "Init";
 //BA.debugLineNum = 102;BA.debugLine="Dim pws As PhoneWakeState";
_pws = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 104;BA.debugLine="Public salvandoScan As Boolean";
_salvandoscan = false;
 //BA.debugLineNum = 105;BA.debugLine="Public CBsalvar As Boolean";
_cbsalvar = false;
 //BA.debugLineNum = 106;BA.debugLine="Dim nlinea As Int";
_nlinea = 0;
 //BA.debugLineNum = 107;BA.debugLine="Public secuencia As Int";
_secuencia = 0;
 //BA.debugLineNum = 108;BA.debugLine="Public RxCount As Int";
_rxcount = 0;
 //BA.debugLineNum = 110;BA.debugLine="Public arrancado As Boolean";
_arrancado = false;
 //BA.debugLineNum = 111;BA.debugLine="Public esperapelin As Int";
_esperapelin = 0;
 //BA.debugLineNum = 112;BA.debugLine="Private ConnectedServices As List";
_connectedservices = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 113;BA.debugLine="Public ConnectedName As String";
_connectedname = "";
 //BA.debugLineNum = 114;BA.debugLine="Public manager As BleManager2";
_manager = new anywheresoftware.b4a.objects.BleManager2();
 //BA.debugLineNum = 115;BA.debugLine="Public currentStateText As String = \"UNKNOWN\"";
_currentstatetext = "UNKNOWN";
 //BA.debugLineNum = 116;BA.debugLine="Public currentState As Int";
_currentstate = 0;
 //BA.debugLineNum = 117;BA.debugLine="Public BTrssi As Int";
_btrssi = 0;
 //BA.debugLineNum = 118;BA.debugLine="Public connected As Boolean = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 119;BA.debugLine="Public Vbat As Float";
_vbat = 0f;
 //BA.debugLineNum = 120;BA.debugLine="Public DeltaIR As Float";
_deltair = 0f;
 //BA.debugLineNum = 121;BA.debugLine="Public DeltaMag As Float";
_deltamag = 0f;
 //BA.debugLineNum = 123;BA.debugLine="Public SegScan As Int = 0";
_segscan = (int) (0);
 //BA.debugLineNum = 124;BA.debugLine="Public Es155espera As Int = 0";
_es155espera = (int) (0);
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _readdata() throws Exception{
String _s = "";
 //BA.debugLineNum = 230;BA.debugLine="Public Sub ReadData";
 //BA.debugLineNum = 231;BA.debugLine="For Each s As String In ConnectedServices";
{
final anywheresoftware.b4a.BA.IterableList group1 = _connectedservices;
final int groupLen1 = group1.getSize()
;int index1 = 0;
;
for (; index1 < groupLen1;index1++){
_s = BA.ObjectToString(group1.Get(index1));
 //BA.debugLineNum = 233;BA.debugLine="If s = SERVICE_UUID Then";
if ((_s).equals(_service_uuid)) { 
 //BA.debugLineNum = 235;BA.debugLine="Try";
try { //BA.debugLineNum = 236;BA.debugLine="manager.SetNotify (s, CHARACTERISTIC_UUID_RX,";
_manager.SetNotify(_s,_characteristic_uuid_rx,anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 238;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("63014664",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 //BA.debugLineNum = 239;BA.debugLine="Log (\" Notify Exception\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63014665"," Notify Exception",0);
 };
 };
 }
};
 //BA.debugLineNum = 243;BA.debugLine="End Sub";
return "";
}
public static String  _scaneabt() throws Exception{
 //BA.debugLineNum = 663;BA.debugLine="Public Sub ScaneaBT";
 //BA.debugLineNum = 671;BA.debugLine="Log(\"ScanBT\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63801096","ScanBT",0);
 //BA.debugLineNum = 672;BA.debugLine="StartScan";
_startscan();
 //BA.debugLineNum = 674;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 128;BA.debugLine="manager.Initialize(\"manager\")";
_manager.Initialize(processBA,"manager");
 //BA.debugLineNum = 130;BA.debugLine="arrancado = False";
_arrancado = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 131;BA.debugLine="esperapelin = 0";
_esperapelin = (int) (0);
 //BA.debugLineNum = 150;BA.debugLine="TiempoSalvarSg = 10";
_tiemposalvarsg = (int) (10);
 //BA.debugLineNum = 151;BA.debugLine="Lat = 0";
_lat = BA.NumberToString(0);
 //BA.debugLineNum = 152;BA.debugLine="Lon = 0";
_lon = BA.NumberToString(0);
 //BA.debugLineNum = 153;BA.debugLine="Alt = 0";
_alt = (float) (0);
 //BA.debugLineNum = 154;BA.debugLine="EsperaMsg = 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 155;BA.debugLine="DeltaIR = 2.0";
_deltair = (float) (2.0);
 //BA.debugLineNum = 156;BA.debugLine="DeltaMag = 0.5";
_deltamag = (float) (0.5);
 //BA.debugLineNum = 158;BA.debugLine="Map1.Initialize";
_map1.Initialize();
 //BA.debugLineNum = 159;BA.debugLine="MapBT.Initialize";
_mapbt.Initialize();
 //BA.debugLineNum = 161;BA.debugLine="Tim1sServ.Initialize(\"Tim1sServ\",1000)";
_tim1sserv.Initialize(processBA,"Tim1sServ",(long) (1000));
 //BA.debugLineNum = 163;BA.debugLine="Tim1sServ.Enabled=True";
_tim1sserv.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 164;BA.debugLine="bep.Initialize(100,800)";
_bep.Initialize((int) (100),(int) (800));
 //BA.debugLineNum = 165;BA.debugLine="bepEnd.Initialize(200,600)";
_bepend.Initialize((int) (200),(int) (600));
 //BA.debugLineNum = 167;BA.debugLine="Log(\"service create\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62555944","service create",0);
 //BA.debugLineNum = 169;BA.debugLine="Vbat = 500";
_vbat = (float) (500);
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 220;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 221;BA.debugLine="If Main.udpsocket1.IsInitialized Then";
if (mostCurrent._main._udpsocket1 /*anywheresoftware.b4a.objects.SocketWrapper.UDPSocket*/ .IsInitialized()) { 
 //BA.debugLineNum = 222;BA.debugLine="Main.udpsocket1.Close";
mostCurrent._main._udpsocket1 /*anywheresoftware.b4a.objects.SocketWrapper.UDPSocket*/ .Close();
 };
 //BA.debugLineNum = 224;BA.debugLine="manager.Disconnect";
_manager.Disconnect();
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 193;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 194;BA.debugLine="Service.StartForeground(1, CreateNotification(\"..";
mostCurrent._service.StartForeground((int) (1),(android.app.Notification)(_createnotification("...").getObject()));
 //BA.debugLineNum = 195;BA.debugLine="End Sub";
return "";
}
public static String  _startgps() throws Exception{
 //BA.debugLineNum = 199;BA.debugLine="public Sub StartGPS";
 //BA.debugLineNum = 200;BA.debugLine="GPS1.Initialize(\"GPS\")";
_gps1.Initialize("GPS");
 //BA.debugLineNum = 201;BA.debugLine="GPS1.Start(0, 0) 'Listen to GPS with no filters";
_gps1.Start(processBA,(long) (0),(float) (0));
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _startscan() throws Exception{
anywheresoftware.b4a.objects.collections.List _uuids = null;
 //BA.debugLineNum = 299;BA.debugLine="Public Sub StartScan";
 //BA.debugLineNum = 300;BA.debugLine="Private uuids As List";
_uuids = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 302;BA.debugLine="inform = esperapelin & \" State \"";
_inform = BA.NumberToString(_esperapelin)+" State ";
 //BA.debugLineNum = 303;BA.debugLine="uuids.Initialize";
_uuids.Initialize();
 //BA.debugLineNum = 304;BA.debugLine="uuids.Add(SERVICE_UUID)";
_uuids.Add((Object)(_service_uuid));
 //BA.debugLineNum = 305;BA.debugLine="If manager.State <> manager.STATE_POWERED_ON Then";
if (_manager.getState()!=_manager.STATE_POWERED_ON) { 
 //BA.debugLineNum = 306;BA.debugLine="inform = inform & \"BT OFF.\"";
_inform = _inform+"BT OFF.";
 //BA.debugLineNum = 307;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63276808",_inform,0);
 }else if(_rp.Check(_rp.PERMISSION_ACCESS_COARSE_LOCATION)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 309;BA.debugLine="Log(\"No location permission.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63276810","No location permission.",0);
 }else {
 //BA.debugLineNum = 311;BA.debugLine="manager.Scan2(Null, False)";
_manager.Scan2((anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(anywheresoftware.b4a.keywords.Common.Null)),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 312;BA.debugLine="inform = inform & \" BT ON.\"";
_inform = _inform+" BT ON.";
 //BA.debugLineNum = 313;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63276814",_inform,0);
 };
 //BA.debugLineNum = 316;BA.debugLine="End Sub";
return "";
}
public static String  _tim1sserv_tick() throws Exception{
 //BA.debugLineNum = 691;BA.debugLine="Sub Tim1sServ_Tick";
 //BA.debugLineNum = 693;BA.debugLine="SegScan = SegScan +1";
_segscan = (int) (_segscan+1);
 //BA.debugLineNum = 695;BA.debugLine="esperapelin = esperapelin +1";
_esperapelin = (int) (_esperapelin+1);
 //BA.debugLineNum = 697;BA.debugLine="If gps_permiso_ok = True Then  'primera vez";
if (_gps_permiso_ok==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 698;BA.debugLine="gps_permiso_ok = False";
_gps_permiso_ok = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 699;BA.debugLine="StartGPS";
_startgps();
 //BA.debugLineNum = 700;BA.debugLine="Main.udpsocket1.Initialize(\"UDP\", 2255, 1000)";
mostCurrent._main._udpsocket1 /*anywheresoftware.b4a.objects.SocketWrapper.UDPSocket*/ .Initialize(processBA,"UDP",(int) (2255),(int) (1000));
 };
 //BA.debugLineNum = 703;BA.debugLine="If NameSensor.Contains(\"stars\") Then";
if (_namesensor.contains("stars")) { 
 //BA.debugLineNum = 704;BA.debugLine="Log(\"stars found\")";
anywheresoftware.b4a.keywords.Common.LogImpl("63866637","stars found",0);
 }else {
 //BA.debugLineNum = 706;BA.debugLine="inform = esperapelin";
_inform = BA.NumberToString(_esperapelin);
 //BA.debugLineNum = 707;BA.debugLine="If arrancado = False And esperapelin = 2 Then";
if (_arrancado==anywheresoftware.b4a.keywords.Common.False && _esperapelin==2) { 
 //BA.debugLineNum = 708;BA.debugLine="EsperaMsg = 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 709;BA.debugLine="inform = inform & ( \" Scanin BT \")";
_inform = _inform+(" Scanin BT ");
 //BA.debugLineNum = 710;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63866643",_inform,0);
 //BA.debugLineNum = 711;BA.debugLine="StartScan";
_startscan();
 }else if(_arrancado==anywheresoftware.b4a.keywords.Common.False && (_esperapelin==4 || _esperapelin==5 || _esperapelin==6) && _connected==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 713;BA.debugLine="arrancado = True";
_arrancado = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 714;BA.debugLine="inform = inform & (\" Waiting Data \")";
_inform = _inform+(" Waiting Data ");
 //BA.debugLineNum = 715;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63866648",_inform,0);
 //BA.debugLineNum = 716;BA.debugLine="ReadData";
_readdata();
 };
 //BA.debugLineNum = 721;BA.debugLine="If (EsperaMsg > 10)   Then";
if ((_esperamsg>10)) { 
 //BA.debugLineNum = 722;BA.debugLine="EsperaMsg = 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 723;BA.debugLine="arrancado = False";
_arrancado = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 724;BA.debugLine="esperapelin = 0";
_esperapelin = (int) (0);
 //BA.debugLineNum = 725;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 726;BA.debugLine="inform = inform & ( \" Waiting data > 8 sg\")";
_inform = _inform+(" Waiting data > 8 sg");
 //BA.debugLineNum = 727;BA.debugLine="Log(inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63866660",_inform,0);
 };
 //BA.debugLineNum = 731;BA.debugLine="If (esperapelin > 6) And connected = False  Then";
if ((_esperapelin>6) && _connected==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 732;BA.debugLine="arrancado = False";
_arrancado = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 733;BA.debugLine="EsperaMsg = 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 734;BA.debugLine="esperapelin = 0";
_esperapelin = (int) (0);
 //BA.debugLineNum = 735;BA.debugLine="inform = inform & \" Wait Sensor > 6 sg\"";
_inform = _inform+" Wait Sensor > 6 sg";
 //BA.debugLineNum = 736;BA.debugLine="Log (inform)";
anywheresoftware.b4a.keywords.Common.LogImpl("63866669",_inform,0);
 //BA.debugLineNum = 737;BA.debugLine="manager.Disconnect ' desconecta para empezar de";
_manager.Disconnect();
 };
 };
 //BA.debugLineNum = 742;BA.debugLine="EsperaMsg = EsperaMsg + 1";
_esperamsg = (int) (_esperamsg+1);
 //BA.debugLineNum = 744;BA.debugLine="EsperaSalvar = EsperaSalvar + 1";
_esperasalvar = (int) (_esperasalvar+1);
 //BA.debugLineNum = 745;BA.debugLine="espera = espera+1";
_espera = (int) (_espera+1);
 //BA.debugLineNum = 747;BA.debugLine="If (EstadoCBsabe = True) And secuencia > 200   Th";
if ((_estadocbsabe==anywheresoftware.b4a.keywords.Common.True) && _secuencia>200) { 
 //BA.debugLineNum = 748;BA.debugLine="If (EsperaSalvar >= TiempoSalvarSg  ) Then";
if ((_esperasalvar>=_tiemposalvarsg)) { 
 //BA.debugLineNum = 749;BA.debugLine="EsperaSalvar = 0";
_esperasalvar = (int) (0);
 //BA.debugLineNum = 750;BA.debugLine="If (ContadorR > 0) Then";
if ((_contadorr>0)) { 
 //BA.debugLineNum = 751;BA.debugLine="GuardaDato";
_guardadato();
 //BA.debugLineNum = 752;BA.debugLine="ContadorR = 0";
_contadorr = (int) (0);
 //BA.debugLineNum = 753;BA.debugLine="TObjM = 0";
_tobjm = (float) (0);
 //BA.debugLineNum = 754;BA.debugLine="TAmbM = 0";
_tambm = (float) (0);
 //BA.debugLineNum = 755;BA.debugLine="MagnitudM = 0";
_magnitudm = (float) (0);
 //BA.debugLineNum = 756;BA.debugLine="FrecuenciaM = 0";
_frecuenciam = (float) (0);
 //BA.debugLineNum = 757;BA.debugLine="AlturaM = 0";
_alturam = (float) (0);
 };
 };
 //BA.debugLineNum = 761;BA.debugLine="Tescaneo = Tescaneo +1";
_tescaneo = (int) (_tescaneo+1);
 //BA.debugLineNum = 763;BA.debugLine="If ((EsperaMsg > 20) And (EsperaMsg < 25))   The";
if (((_esperamsg>20) && (_esperamsg<25))) { 
 //BA.debugLineNum = 764;BA.debugLine="If Main.udpsocket1.IsInitialized = False Then";
if (mostCurrent._main._udpsocket1 /*anywheresoftware.b4a.objects.SocketWrapper.UDPSocket*/ .IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 }else {
 };
 //BA.debugLineNum = 770;BA.debugLine="bep.Initialize(400,1000)";
_bep.Initialize((int) (400),(int) (1000));
 //BA.debugLineNum = 771;BA.debugLine="bep.Beep";
_bep.Beep();
 };
 //BA.debugLineNum = 773;BA.debugLine="If (EsperaMsg > 25)   Then ' avisa durante 10sg";
if ((_esperamsg>25)) { 
 //BA.debugLineNum = 774;BA.debugLine="EsperaMsg = 10";
_esperamsg = (int) (10);
 };
 };
 //BA.debugLineNum = 781;BA.debugLine="If FlagAlarmIR = True Then";
if (_flagalarmir==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 782;BA.debugLine="If Abs( TObj - RefAlarmIR) > DeltaIR Then";
if (anywheresoftware.b4a.keywords.Common.Abs(_tobj-_refalarmir)>_deltair) { 
 //BA.debugLineNum = 783;BA.debugLine="bep.Initialize(100,600)";
_bep.Initialize((int) (100),(int) (600));
 //BA.debugLineNum = 784;BA.debugLine="bep.Beep";
_bep.Beep();
 //BA.debugLineNum = 785;BA.debugLine="AlarmIR = True";
_alarmir = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 787;BA.debugLine="AlarmIR = False";
_alarmir = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 790;BA.debugLine="If FlagAlarmMV = True Then";
if (_flagalarmmv==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 791;BA.debugLine="If Abs(Magnitud - RefAlarmMV) > DeltaMag Then";
if (anywheresoftware.b4a.keywords.Common.Abs(_magnitud-_refalarmmv)>_deltamag) { 
 //BA.debugLineNum = 792;BA.debugLine="bep.Initialize(100,800)";
_bep.Initialize((int) (100),(int) (800));
 //BA.debugLineNum = 793;BA.debugLine="bep.Beep";
_bep.Beep();
 //BA.debugLineNum = 794;BA.debugLine="AlarmMV = True";
_alarmmv = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 796;BA.debugLine="AlarmMV = False";
_alarmmv = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 800;BA.debugLine="If Magnitud = 24 Then		' avisa si TAS esta guarda";
if (_magnitud==24) { 
 //BA.debugLineNum = 801;BA.debugLine="PeriodoAlarmaAcustica = PeriodoAlarmaAcustica +1";
_periodoalarmaacustica = (int) (_periodoalarmaacustica+1);
 //BA.debugLineNum = 802;BA.debugLine="If PeriodoAlarmaAcustica > 4 Then";
if (_periodoalarmaacustica>4) { 
 //BA.debugLineNum = 803;BA.debugLine="bep.Initialize(600,2000)";
_bep.Initialize((int) (600),(int) (2000));
 //BA.debugLineNum = 804;BA.debugLine="bep.Beep";
_bep.Beep();
 //BA.debugLineNum = 805;BA.debugLine="PeriodoAlarmaAcustica = 0";
_periodoalarmaacustica = (int) (0);
 //BA.debugLineNum = 806;BA.debugLine="Magnitud = 0";
_magnitud = (float) (0);
 };
 };
 //BA.debugLineNum = 810;BA.debugLine="If Vbat < 3.7 Then	' avisa de bateria baja";
if (_vbat<3.7) { 
 //BA.debugLineNum = 811;BA.debugLine="PeriodoAlarmaAcustica = PeriodoAlarmaAcustica +1";
_periodoalarmaacustica = (int) (_periodoalarmaacustica+1);
 //BA.debugLineNum = 812;BA.debugLine="If PeriodoAlarmaAcustica > 4 Then";
if (_periodoalarmaacustica>4) { 
 //BA.debugLineNum = 813;BA.debugLine="bep.Initialize(200,1500)";
_bep.Initialize((int) (200),(int) (1500));
 //BA.debugLineNum = 814;BA.debugLine="bep.Beep";
_bep.Beep();
 //BA.debugLineNum = 815;BA.debugLine="PeriodoAlarmaAcustica = 0";
_periodoalarmaacustica = (int) (0);
 //BA.debugLineNum = 816;BA.debugLine="Vbat = 500";
_vbat = (float) (500);
 };
 };
 //BA.debugLineNum = 822;BA.debugLine="End Sub";
return "";
}
public static String  _udp_packetarrived(anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _packet) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _p = null;
 //BA.debugLineNum = 554;BA.debugLine="Sub UDP_PacketArrived(Packet As UDPPacket)";
 //BA.debugLineNum = 555;BA.debugLine="Dim NameSensorRx As String";
_namesensorrx = "";
 //BA.debugLineNum = 561;BA.debugLine="msg = BytesToString(Packet.Data, Packet.Offset, P";
_msg = anywheresoftware.b4a.keywords.Common.BytesToString(_packet.getData(),_packet.getOffset(),_packet.getLength(),"UTF-8");
 //BA.debugLineNum = 563;BA.debugLine="Dim p As JSONParser";
_p = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 564;BA.debugLine="p.Initialize(msg)";
_p.Initialize(_msg);
 //BA.debugLineNum = 567;BA.debugLine="Map1 = p.NextObject";
_map1 = _p.NextObject();
 //BA.debugLineNum = 568;BA.debugLine="NameSensorRx = Map1.Get(\"name\")";
_namesensorrx = BA.ObjectToString(_map1.Get((Object)("name")));
 //BA.debugLineNum = 569;BA.debugLine="dirurl = Packet.HostAddress";
_dirurl = _packet.getHostAddress();
 //BA.debugLineNum = 571;BA.debugLine="CuentaUDP = CuentaUDP + 1";
_cuentaudp = (int) (_cuentaudp+1);
 //BA.debugLineNum = 573;BA.debugLine="If (NameSensorRx = NameSensor) Then";
if (((_namesensorrx).equals(_namesensor))) { 
 //BA.debugLineNum = 575;BA.debugLine="EsperaMsg = 0";
_esperamsg = (int) (0);
 //BA.debugLineNum = 576;BA.debugLine="DatoBle = False";
_datoble = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 577;BA.debugLine="If (NameSensorRx.StartsWith( \"TESS-P\") ) Then";
if ((_namesensorrx.startsWith("TESS-P"))) { 
 //BA.debugLineNum = 578;BA.debugLine="MyTess = True";
_mytess = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 580;BA.debugLine="MyTess = False";
_mytess = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 583;BA.debugLine="dirurlSelec = dirurl";
_dirurlselec = _dirurl;
 //BA.debugLineNum = 584;BA.debugLine="Magnitud =  Map1.Get(\"mag\")";
_magnitud = (float)(BA.ObjectToNumber(_map1.Get((Object)("mag"))));
 //BA.debugLineNum = 585;BA.debugLine="Frecuencia = Map1.Get(\"freq\")";
_frecuencia = (float)(BA.ObjectToNumber(_map1.Get((Object)("freq"))));
 //BA.debugLineNum = 589;BA.debugLine="Try";
try { //BA.debugLineNum = 590;BA.debugLine="SeqUdp = Map1.Get(\"seq\")";
_sequdp = (int)(BA.ObjectToNumber(_map1.Get((Object)("seq"))));
 } 
       catch (Exception e23) {
			processBA.setLastException(e23); //BA.debugLineNum = 593;BA.debugLine="SeqUdp = Map1.Get(\"udp\")";
_sequdp = (int)(BA.ObjectToNumber(_map1.Get((Object)("udp"))));
 };
 //BA.debugLineNum = 597;BA.debugLine="If (SeqUdp - SeqUdpOld ) > 1 Then";
if ((_sequdp-_sequdpold)>1) { 
 //BA.debugLineNum = 598;BA.debugLine="UdpLost = UdpLost + 1";
_udplost = (int) (_udplost+1);
 };
 //BA.debugLineNum = 600;BA.debugLine="SeqUdpOld = SeqUdp";
_sequdpold = _sequdp;
 //BA.debugLineNum = 602;BA.debugLine="Try";
try { //BA.debugLineNum = 603;BA.debugLine="TAmb = Map1.Get(\"tamb\")";
_tamb = (float)(BA.ObjectToNumber(_map1.Get((Object)("tamb"))));
 //BA.debugLineNum = 604;BA.debugLine="TObj = Map1.Get(\"tsky\")";
_tobj = (float)(BA.ObjectToNumber(_map1.Get((Object)("tsky"))));
 } 
       catch (Exception e33) {
			processBA.setLastException(e33); //BA.debugLineNum = 607;BA.debugLine="TAmb = 0";
_tamb = (float) (0);
 //BA.debugLineNum = 608;BA.debugLine="TObj = 0";
_tobj = (float) (0);
 };
 //BA.debugLineNum = 618;BA.debugLine="Try";
try { //BA.debugLineNum = 620;BA.debugLine="Altura = Map1.Get(\"alt\")";
_altura = (float)(BA.ObjectToNumber(_map1.Get((Object)("alt"))));
 //BA.debugLineNum = 621;BA.debugLine="Azimut = Map1.Get(\"azi\")";
_azimut = (float)(BA.ObjectToNumber(_map1.Get((Object)("azi"))));
 //BA.debugLineNum = 622;BA.debugLine="ZPoint = Map1.Get(\"ZP\")";
_zpoint = (float)(BA.ObjectToNumber(_map1.Get((Object)("ZP"))));
 } 
       catch (Exception e41) {
			processBA.setLastException(e41); //BA.debugLineNum = 624;BA.debugLine="Altura = 0";
_altura = (float) (0);
 //BA.debugLineNum = 625;BA.debugLine="Azimut = 0";
_azimut = (float) (0);
 //BA.debugLineNum = 626;BA.debugLine="ZPoint = 0";
_zpoint = (float) (0);
 };
 //BA.debugLineNum = 630;BA.debugLine="If (EstadoCBsabe = True)  Then";
if ((_estadocbsabe==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 631;BA.debugLine="ContadorR =  ContadorR + 1";
_contadorr = (int) (_contadorr+1);
 //BA.debugLineNum = 632;BA.debugLine="FrecuenciaM = FrecuenciaM + Frecuencia";
_frecuenciam = (float) (_frecuenciam+_frecuencia);
 //BA.debugLineNum = 633;BA.debugLine="MagnitudM = MagnitudM + Magnitud";
_magnitudm = (float) (_magnitudm+_magnitud);
 //BA.debugLineNum = 634;BA.debugLine="TAmbM = TAmbM + TAmb";
_tambm = (float) (_tambm+_tamb);
 //BA.debugLineNum = 635;BA.debugLine="TObjM = TObjM + TObj";
_tobjm = (float) (_tobjm+_tobj);
 //BA.debugLineNum = 636;BA.debugLine="AlturaM = AlturaM + Altura";
_alturam = (float) (_alturam+_altura);
 }else {
 //BA.debugLineNum = 639;BA.debugLine="ContadorR = 1";
_contadorr = (int) (1);
 //BA.debugLineNum = 640;BA.debugLine="FrecuenciaM = Frecuencia";
_frecuenciam = _frecuencia;
 //BA.debugLineNum = 641;BA.debugLine="MagnitudM =  Magnitud";
_magnitudm = _magnitud;
 //BA.debugLineNum = 642;BA.debugLine="TAmbM =  TAmb";
_tambm = _tamb;
 //BA.debugLineNum = 643;BA.debugLine="TObjM =  TObj";
_tobjm = _tobj;
 //BA.debugLineNum = 644;BA.debugLine="AlturaM =  Altura";
_alturam = _altura;
 //BA.debugLineNum = 645;BA.debugLine="AzimutM =  Azimut";
_azimutm = _azimut;
 };
 }else {
 //BA.debugLineNum = 650;BA.debugLine="NameSensorRxOtro = NameSensorRx";
_namesensorrxotro = _namesensorrx;
 //BA.debugLineNum = 651;BA.debugLine="OtrosUdp = True";
_otrosudp = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 658;BA.debugLine="End Sub";
return "";
}
}
