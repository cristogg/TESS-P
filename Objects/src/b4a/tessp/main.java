package b4a.tessp;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.tessp", "b4a.tessp.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.tessp", "b4a.tessp.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.tessp.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _tim1s = null;
public static anywheresoftware.b4a.objects.Timer _timboton = null;
public static String _mqtt_server_local = "";
public static String _mqtt_user_local = "";
public static String _mqtt_password_local = "";
public static String _my_topic = "";
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _myconmq = null;
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _mqtt = null;
public static boolean _mqconectado = false;
public static int _difacimut = 0;
public static String _latlongsl = "";
public static String _lati = "";
public static String _longi = "";
public static String _horasl = "";
public static String _hora = "";
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static float _brillomax = 0f;
public static float _brillomin = 0f;
public static float _mvmin = 0f;
public static float _mvmax = 0f;
public static float _tempirmin = 0f;
public static float _tempamb = 0f;
public static float _rango = 0f;
public static float[][] _mag2 = null;
public static int _ancho = 0;
public static int _alto = 0;
public static anywheresoftware.b4a.objects.B4XCanvas _can = null;
public static String _fentrada = "";
public static anywheresoftware.b4a.objects.B4XCanvas _grafica = null;
public static float _magcenit = 0f;
public static String _fichsel = "";
public static anywheresoftware.b4a.objects.RuntimePermissions _rpcfg = null;
public static anywheresoftware.b4a.randomaccessfile.RandomAccessFile _tsp_cfg = null;
public static boolean _scaner = false;
public static int _altertxt = 0;
public static float[] _magas = null;
public static boolean _as145 = false;
public static int[] _mr = null;
public static int[] _mg = null;
public static int[] _mb = null;
public static String _colorgrafo = "";
public static anywheresoftware.b4a.objects.SocketWrapper.UDPSocket _udpsocket1 = null;
public static int _numrowreal = 0;
public static boolean _avisosensorfound = false;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _ltsensor = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label11 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cbsave = null;
public anywheresoftware.b4a.objects.LabelWrapper _label5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bmqtt = null;
public anywheresoftware.b4a.objects.LabelWrapper _lorientacion = null;
public anywheresoftware.b4a.objects.LabelWrapper _lcuenta = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _tbtiempo = null;
public anywheresoftware.b4a.objects.EditTextWrapper _elugar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bgraph = null;
public anywheresoftware.b4a.objects.PanelWrapper _panelmain = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lversion = null;
public anywheresoftware.b4a.objects.LabelWrapper _lnelm = null;
public anywheresoftware.b4a.objects.LabelWrapper _ltempir = null;
public anywheresoftware.b4a.objects.ButtonWrapper _balarmair = null;
public anywheresoftware.b4a.objects.ButtonWrapper _balarmamv = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cbcolor = null;
public anywheresoftware.b4a.objects.LabelWrapper _lvbat = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cbtir = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bscan = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndisconnect = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreaddata = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bstarttas = null;
public anywheresoftware.b4a.objects.LabelWrapper _linform = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public b4a.tessp.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static boolean  _abrirfile(String _nombrefich) throws Exception{
float _sealev = 0f;
float _altura = 0f;
float _alturaold = 0f;
float _acimut = 0f;
float _acimutold = 0f;
String _fsalida = "";
int _espera = 0;
int _numrow = 0;
int _nuevarow = 0;
int _nc = 0;
String _strsalida = "";
String _strlinea = "";
anywheresoftware.b4a.objects.collections.List _head = null;
String _contenido = "";
anywheresoftware.b4a.objects.StringUtils _su = null;
anywheresoftware.b4a.objects.collections.List _table = null;
String[] _row = null;
String _corto = "";
 //BA.debugLineNum = 925;BA.debugLine="Sub abrirfile (NombreFich As String) As Boolean  '";
 //BA.debugLineNum = 927;BA.debugLine="Private sealev As Float";
_sealev = 0f;
 //BA.debugLineNum = 928;BA.debugLine="Private altura, alturaold As Float";
_altura = 0f;
_alturaold = 0f;
 //BA.debugLineNum = 929;BA.debugLine="Private acimut, acimutold As Float";
_acimut = 0f;
_acimutold = 0f;
 //BA.debugLineNum = 931;BA.debugLine="Private fsalida As String";
_fsalida = "";
 //BA.debugLineNum = 933;BA.debugLine="Private espera, numrow, nuevarow, nc As Int";
_espera = 0;
_numrow = 0;
_nuevarow = 0;
_nc = 0;
 //BA.debugLineNum = 934;BA.debugLine="Private strsalida, strlinea As String";
_strsalida = "";
_strlinea = "";
 //BA.debugLineNum = 935;BA.debugLine="Private head As List";
_head = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 936;BA.debugLine="Private contenido As String";
_contenido = "";
 //BA.debugLineNum = 941;BA.debugLine="fentrada = NombreFich";
_fentrada = _nombrefich;
 //BA.debugLineNum = 945;BA.debugLine="fsalida = \"sel.txt\"";
_fsalida = "sel.txt";
 //BA.debugLineNum = 946;BA.debugLine="TempIRmin = 100";
_tempirmin = (float) (100);
 //BA.debugLineNum = 947;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 950;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),_nombrefich)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 951;BA.debugLine="Dim su As StringUtils";
_su = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 952;BA.debugLine="Dim Table As List";
_table = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 955;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),_fsalida)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 957;BA.debugLine="File.Delete(rpcfg.GetSafeDirDefaultExternal(\"\")";
anywheresoftware.b4a.keywords.Common.File.Delete(_rpcfg.GetSafeDirDefaultExternal(""),_fsalida);
 };
 //BA.debugLineNum = 959;BA.debugLine="strsalida = \"\"";
_strsalida = "";
 //BA.debugLineNum = 961;BA.debugLine="contenido = File.ReadString(rpcfg.GetSafeDirDefa";
_contenido = anywheresoftware.b4a.keywords.Common.File.ReadString(_rpcfg.GetSafeDirDefaultExternal(""),_nombrefich);
 //BA.debugLineNum = 963;BA.debugLine="If contenido.IndexOf(\"#\") = 0 Then";
if (_contenido.indexOf("#")==0) { 
 //BA.debugLineNum = 964;BA.debugLine="If contenido.LastIndexOf(\"#\") > 0 Then";
if (_contenido.lastIndexOf("#")>0) { 
 //BA.debugLineNum = 965;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 966;BA.debugLine="Label5.Text = \"No scan file.\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("No scan file."));
 //BA.debugLineNum = 967;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 970;BA.debugLine="Table = su.LoadCSV2( rpcfg.GetSafeDirDefaultExt";
_table = _su.LoadCSV2(_rpcfg.GetSafeDirDefaultExternal(""),_nombrefich,anywheresoftware.b4a.keywords.Common.Chr((int) (9)),_head);
 //BA.debugLineNum = 971;BA.debugLine="For Each Row() As String In Table";
{
final anywheresoftware.b4a.BA.IterableList group28 = _table;
final int groupLen28 = group28.getSize()
;int index28 = 0;
;
for (; index28 < groupLen28;index28++){
_row = (String[])(group28.Get(index28));
 //BA.debugLineNum = 972;BA.debugLine="numrow = numrow + 1";
_numrow = (int) (_numrow+1);
 //BA.debugLineNum = 973;BA.debugLine="If (Row(3) < TempIRmin) Then";
if (((double)(Double.parseDouble(_row[(int) (3)]))<_tempirmin)) { 
 //BA.debugLineNum = 974;BA.debugLine="TempIRmin = Row(3)";
_tempirmin = (float)(Double.parseDouble(_row[(int) (3)]));
 //BA.debugLineNum = 975;BA.debugLine="TempAmb = Row(4)";
_tempamb = (float)(Double.parseDouble(_row[(int) (4)]));
 };
 //BA.debugLineNum = 977;BA.debugLine="altura = Row(6)";
_altura = (float)(Double.parseDouble(_row[(int) (6)]));
 //BA.debugLineNum = 979;BA.debugLine="altura = Row(7)";
_altura = (float)(Double.parseDouble(_row[(int) (7)]));
 //BA.debugLineNum = 980;BA.debugLine="acimut = Row(8)";
_acimut = (float)(Double.parseDouble(_row[(int) (8)]));
 //BA.debugLineNum = 981;BA.debugLine="Try";
try { //BA.debugLineNum = 983;BA.debugLine="sealev = Row(11) 'cuando no hay GPS parece fa";
_sealev = (float)(Double.parseDouble(_row[(int) (11)]));
 } 
       catch (Exception e40) {
			processBA.setLastException(e40); //BA.debugLineNum = 986;BA.debugLine="sealev = 0";
_sealev = (float) (0);
 };
 //BA.debugLineNum = 990;BA.debugLine="caldifacimut( acimut, acimutold)";
_caldifacimut((int) (_acimut),(int) (_acimutold));
 //BA.debugLineNum = 992;BA.debugLine="If (altura > alturaold + 3) Or (altura < altur";
if ((_altura>_alturaold+3) || (_altura<_alturaold-3) || (_difacimut>14)) { 
 //BA.debugLineNum = 993;BA.debugLine="espera = espera + 1";
_espera = (int) (_espera+1);
 //BA.debugLineNum = 994;BA.debugLine="If espera > 4 Then";
if (_espera>4) { 
 //BA.debugLineNum = 995;BA.debugLine="espera = 0";
_espera = (int) (0);
 //BA.debugLineNum = 996;BA.debugLine="nuevarow = nuevarow + 1";
_nuevarow = (int) (_nuevarow+1);
 //BA.debugLineNum = 997;BA.debugLine="alturaold = altura";
_alturaold = _altura;
 //BA.debugLineNum = 998;BA.debugLine="acimutold = acimut";
_acimutold = _acimut;
 //BA.debugLineNum = 1000;BA.debugLine="If nuevarow < 49 Then";
if (_nuevarow<49) { 
 //BA.debugLineNum = 1002;BA.debugLine="Private corto As String";
_corto = "";
 //BA.debugLineNum = 1004;BA.debugLine="corto = Row(10).SubString2(0, Row(10).Index";
_corto = _row[(int) (10)].substring((int) (0),(int) (_row[(int) (10)].indexOf(",")+2));
 //BA.debugLineNum = 1007;BA.debugLine="LatLongSl = Row(1) & \"  \" & Row(2) & \"    L";
_latlongsl = _row[(int) (1)]+"  "+_row[(int) (2)]+"    Lat.: "+_row[(int) (9)].substring((int) (0),(int) (_row[(int) (9)].indexOf(",")+2))+"   Long.: "+_corto;
 //BA.debugLineNum = 1008;BA.debugLine="Lati = \" Lat: \" & Row(9).SubString2(0, Row(";
_lati = " Lat: "+_row[(int) (9)].substring((int) (0),(int) (_row[(int) (9)].indexOf(",")+2));
 //BA.debugLineNum = 1009;BA.debugLine="Longi = \" Lon: \" & corto";
_longi = " Lon: "+_corto;
 //BA.debugLineNum = 1010;BA.debugLine="HoraSl = \" SL: \" & sealev & \" m\"";
_horasl = " SL: "+BA.NumberToString(_sealev)+" m";
 //BA.debugLineNum = 1012;BA.debugLine="Hora =  Row(2)";
_hora = _row[(int) (2)];
 //BA.debugLineNum = 1014;BA.debugLine="For nc = 0 To Row.Length-1";
{
final int step58 = 1;
final int limit58 = (int) (_row.length-1);
_nc = (int) (0) ;
for (;_nc <= limit58 ;_nc = _nc + step58 ) {
 //BA.debugLineNum = 1015;BA.debugLine="strsalida = strsalida  & Row(nc)	& \"	\"";
_strsalida = _strsalida+_row[_nc]+"	";
 //BA.debugLineNum = 1016;BA.debugLine="strlinea = strlinea  & Row(nc)	& \"	\"";
_strlinea = _strlinea+_row[_nc]+"	";
 }
};
 //BA.debugLineNum = 1019;BA.debugLine="If nuevarow < 48 Then";
if (_nuevarow<48) { 
 //BA.debugLineNum = 1020;BA.debugLine="strsalida = strsalida & CRLF";
_strsalida = _strsalida+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 1021;BA.debugLine="strlinea = strlinea & CRLF";
_strlinea = _strlinea+anywheresoftware.b4a.keywords.Common.CRLF;
 };
 };
 };
 };
 }
};
 //BA.debugLineNum = 1030;BA.debugLine="If nuevarow > 47 Then";
if (_nuevarow>47) { 
 //BA.debugLineNum = 1031;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1032;BA.debugLine="Label5.Text = \"OK file. Total rows found: \" &";
mostCurrent._label5.setText(BA.ObjectToCharSequence("OK file. Total rows found: "+BA.NumberToString(_nuevarow)));
 //BA.debugLineNum = 1034;BA.debugLine="Try";
try { //BA.debugLineNum = 1035;BA.debugLine="File.WriteString(rpcfg.GetSafeDirDefaultExter";
anywheresoftware.b4a.keywords.Common.File.WriteString(_rpcfg.GetSafeDirDefaultExternal(""),_fsalida,_strsalida);
 } 
       catch (Exception e76) {
			processBA.setLastException(e76); //BA.debugLineNum = 1037;BA.debugLine="ToastMessageShow(\"Can't write file.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Can't write file."),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1039;BA.debugLine="Return(True)";
if (true) return (anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1041;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1042;BA.debugLine="Label5.Text = NombreFich &  \" Invalid scan fil";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombrefich+" Invalid scan file. Rows < 48: "+BA.NumberToString(_nuevarow)));
 //BA.debugLineNum = 1043;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 1048;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1049;BA.debugLine="Label5.Text = \"Invalid scan file.\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Invalid scan file."));
 //BA.debugLineNum = 1050;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1052;BA.debugLine="Return(True)";
if (true) return (anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1056;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1057;BA.debugLine="Label5.Text = NombreFich & \"  not found. \"";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombrefich+"  not found. "));
 //BA.debugLineNum = 1058;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1061;BA.debugLine="End Sub";
return false;
}
public static boolean  _abrirfile145(String _nombre) throws Exception{
float _sealev = 0f;
float _altura = 0f;
float _alturaold = 0f;
float _acimut = 0f;
float _acimutold = 0f;
String _fsalida = "";
int _numrow = 0;
int _anterow = 0;
String _strsalida = "";
String _strlinea = "";
anywheresoftware.b4a.objects.collections.List _head = null;
String _contenido = "";
anywheresoftware.b4a.objects.StringUtils _su = null;
anywheresoftware.b4a.objects.collections.List _table = null;
int _nc = 0;
String[] _row = null;
String _corto = "";
 //BA.debugLineNum = 1063;BA.debugLine="Sub abrirfile145 (Nombre As String) As Boolean  '";
 //BA.debugLineNum = 1065;BA.debugLine="Private sealev As Float";
_sealev = 0f;
 //BA.debugLineNum = 1066;BA.debugLine="Private altura, alturaold As Float";
_altura = 0f;
_alturaold = 0f;
 //BA.debugLineNum = 1067;BA.debugLine="Private acimut, acimutold As Float";
_acimut = 0f;
_acimutold = 0f;
 //BA.debugLineNum = 1069;BA.debugLine="Private fsalida As String";
_fsalida = "";
 //BA.debugLineNum = 1071;BA.debugLine="Private  numrow, anterow As Int";
_numrow = 0;
_anterow = 0;
 //BA.debugLineNum = 1072;BA.debugLine="Private strsalida, strlinea As String";
_strsalida = "";
_strlinea = "";
 //BA.debugLineNum = 1073;BA.debugLine="Private head As List";
_head = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 1074;BA.debugLine="Private contenido As String";
_contenido = "";
 //BA.debugLineNum = 1076;BA.debugLine="fentrada = Nombre";
_fentrada = _nombre;
 //BA.debugLineNum = 1077;BA.debugLine="fsalida = \"sel.txt\"";
_fsalida = "sel.txt";
 //BA.debugLineNum = 1078;BA.debugLine="TempIRmin = 100";
_tempirmin = (float) (100);
 //BA.debugLineNum = 1079;BA.debugLine="numrowreal = 0";
_numrowreal = (int) (0);
 //BA.debugLineNum = 1080;BA.debugLine="anterow = 0";
_anterow = (int) (0);
 //BA.debugLineNum = 1082;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),_nombre)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1083;BA.debugLine="Dim su As StringUtils";
_su = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 1084;BA.debugLine="Dim Table As List";
_table = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 1086;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),_fsalida)==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1088;BA.debugLine="File.Delete(rpcfg.GetSafeDirDefaultExternal(\"\")";
anywheresoftware.b4a.keywords.Common.File.Delete(_rpcfg.GetSafeDirDefaultExternal(""),_fsalida);
 };
 //BA.debugLineNum = 1090;BA.debugLine="strsalida = \"\"";
_strsalida = "";
 //BA.debugLineNum = 1093;BA.debugLine="contenido = File.ReadString(rpcfg.GetSafeDirDefa";
_contenido = anywheresoftware.b4a.keywords.Common.File.ReadString(_rpcfg.GetSafeDirDefaultExternal(""),_nombre);
 //BA.debugLineNum = 1096;BA.debugLine="If contenido.IndexOf(\"#\") = 0 Then";
if (_contenido.indexOf("#")==0) { 
 //BA.debugLineNum = 1097;BA.debugLine="If contenido.LastIndexOf(\"#\") > 0 Then";
if (_contenido.lastIndexOf("#")>0) { 
 //BA.debugLineNum = 1100;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1101;BA.debugLine="Label5.Text = \"Invalid file.\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Invalid file."));
 //BA.debugLineNum = 1103;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1107;BA.debugLine="Table = su.LoadCSV2( rpcfg.GetSafeDirDefaultExt";
_table = _su.LoadCSV2(_rpcfg.GetSafeDirDefaultExternal(""),_nombre,anywheresoftware.b4a.keywords.Common.Chr((int) (9)),_head);
 //BA.debugLineNum = 1108;BA.debugLine="MVmin = 100";
_mvmin = (float) (100);
 //BA.debugLineNum = 1109;BA.debugLine="MVmax = 0";
_mvmax = (float) (0);
 //BA.debugLineNum = 1110;BA.debugLine="For nc = 0 To  145";
{
final int step31 = 1;
final int limit31 = (int) (145);
_nc = (int) (0) ;
for (;_nc <= limit31 ;_nc = _nc + step31 ) {
 //BA.debugLineNum = 1111;BA.debugLine="MagAS(nc) = 22";
_magas[_nc] = (float) (22);
 }
};
 //BA.debugLineNum = 1115;BA.debugLine="For Each row() As String In Table";
{
final anywheresoftware.b4a.BA.IterableList group34 = _table;
final int groupLen34 = group34.getSize()
;int index34 = 0;
;
for (; index34 < groupLen34;index34++){
_row = (String[])(group34.Get(index34));
 //BA.debugLineNum = 1116;BA.debugLine="numrow = row(0)";
_numrow = (int)(Double.parseDouble(_row[(int) (0)]));
 //BA.debugLineNum = 1117;BA.debugLine="If numrow = 0 Then";
if (_numrow==0) { 
 //BA.debugLineNum = 1118;BA.debugLine="numrow = 1";
_numrow = (int) (1);
 };
 //BA.debugLineNum = 1120;BA.debugLine="numrowreal = numrowreal + 1";
_numrowreal = (int) (_numrowreal+1);
 //BA.debugLineNum = 1122;BA.debugLine="If numrow = anterow + 1  Then";
if (_numrow==_anterow+1) { 
 }else if(_numrow==_anterow+2) { 
 //BA.debugLineNum = 1125;BA.debugLine="If CBtir.Checked = True Then";
if (mostCurrent._cbtir.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1126;BA.debugLine="MagAS(numrow-1) = row(3)";
_magas[(int) (_numrow-1)] = (float)(Double.parseDouble(_row[(int) (3)]));
 }else {
 //BA.debugLineNum = 1128;BA.debugLine="MagAS(numrow-1) = row(5)";
_magas[(int) (_numrow-1)] = (float)(Double.parseDouble(_row[(int) (5)]));
 };
 }else if(_numrow==_anterow+3) { 
 //BA.debugLineNum = 1133;BA.debugLine="If CBtir.Checked = True Then";
if (mostCurrent._cbtir.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1134;BA.debugLine="MagAS(numrow-1) = row(3)";
_magas[(int) (_numrow-1)] = (float)(Double.parseDouble(_row[(int) (3)]));
 //BA.debugLineNum = 1135;BA.debugLine="MagAS(numrow-2) = row(3)";
_magas[(int) (_numrow-2)] = (float)(Double.parseDouble(_row[(int) (3)]));
 }else {
 //BA.debugLineNum = 1137;BA.debugLine="MagAS(numrow-1) = row(5)";
_magas[(int) (_numrow-1)] = (float)(Double.parseDouble(_row[(int) (5)]));
 //BA.debugLineNum = 1138;BA.debugLine="MagAS(numrow-2) = row(5)";
_magas[(int) (_numrow-2)] = (float)(Double.parseDouble(_row[(int) (5)]));
 };
 };
 //BA.debugLineNum = 1141;BA.debugLine="anterow = numrow";
_anterow = _numrow;
 //BA.debugLineNum = 1143;BA.debugLine="altura = row(7)";
_altura = (float)(Double.parseDouble(_row[(int) (7)]));
 //BA.debugLineNum = 1144;BA.debugLine="acimut = row(8)";
_acimut = (float)(Double.parseDouble(_row[(int) (8)]));
 //BA.debugLineNum = 1145;BA.debugLine="If CBtir.Checked = True Then";
if (mostCurrent._cbtir.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1146;BA.debugLine="MagAS(numrow) = row(3)";
_magas[_numrow] = (float)(Double.parseDouble(_row[(int) (3)]));
 }else {
 //BA.debugLineNum = 1148;BA.debugLine="MagAS(numrow) = row(5)";
_magas[_numrow] = (float)(Double.parseDouble(_row[(int) (5)]));
 };
 //BA.debugLineNum = 1150;BA.debugLine="Try";
try { //BA.debugLineNum = 1151;BA.debugLine="sealev = row(11) 'cuando no hay GPS parece fa";
_sealev = (float)(Double.parseDouble(_row[(int) (11)]));
 } 
       catch (Exception e67) {
			processBA.setLastException(e67); //BA.debugLineNum = 1153;BA.debugLine="sealev = 0";
_sealev = (float) (0);
 };
 //BA.debugLineNum = 1156;BA.debugLine="If (row(3) < TempIRmin) Then";
if (((double)(Double.parseDouble(_row[(int) (3)]))<_tempirmin)) { 
 //BA.debugLineNum = 1157;BA.debugLine="TempIRmin = row(3)";
_tempirmin = (float)(Double.parseDouble(_row[(int) (3)]));
 //BA.debugLineNum = 1158;BA.debugLine="TempAmb = row(4)";
_tempamb = (float)(Double.parseDouble(_row[(int) (4)]));
 };
 //BA.debugLineNum = 1162;BA.debugLine="If MagAS(numrow) < MVmin Then";
if (_magas[_numrow]<_mvmin) { 
 //BA.debugLineNum = 1163;BA.debugLine="MVmin = MagAS(numrow)";
_mvmin = _magas[_numrow];
 }else if(_magas[_numrow]>_mvmax) { 
 //BA.debugLineNum = 1165;BA.debugLine="MVmax = MagAS(numrow)";
_mvmax = _magas[_numrow];
 };
 //BA.debugLineNum = 1169;BA.debugLine="Private corto As String";
_corto = "";
 //BA.debugLineNum = 1170;BA.debugLine="corto = row(10).SubString2(0, row(10).IndexOf(";
_corto = _row[(int) (10)].substring((int) (0),(int) (_row[(int) (10)].indexOf(",")+2));
 //BA.debugLineNum = 1171;BA.debugLine="LatLongSl =  \"  \" & row(2) & \" Lat: \" &  row(9";
_latlongsl = "  "+_row[(int) (2)]+" Lat: "+_row[(int) (9)].substring((int) (0),(int) (_row[(int) (9)].indexOf(",")+2))+"  Lon: "+_corto;
 //BA.debugLineNum = 1172;BA.debugLine="LatLongSl = LatLongSl  & \"  SL: \" & sealev";
_latlongsl = _latlongsl+"  SL: "+BA.NumberToString(_sealev);
 //BA.debugLineNum = 1173;BA.debugLine="Lati = \" Lat: \" & row(9).SubString2(0, row(9).";
_lati = " Lat: "+_row[(int) (9)].substring((int) (0),(int) (_row[(int) (9)].indexOf(",")+2));
 //BA.debugLineNum = 1174;BA.debugLine="Longi = \" Lon: \" & corto";
_longi = " Lon: "+_corto;
 //BA.debugLineNum = 1175;BA.debugLine="HoraSl = \" SL: \" & sealev & \" m\"";
_horasl = " SL: "+BA.NumberToString(_sealev)+" m";
 }
};
 //BA.debugLineNum = 1179;BA.debugLine="If numrowreal = 145 Then";
if (_numrowreal==145) { 
 //BA.debugLineNum = 1181;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1182;BA.debugLine="Label5.Text = Nombre & \"   OK\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombre+"   OK"));
 }else {
 //BA.debugLineNum = 1186;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1187;BA.debugLine="Label5.Text = Nombre & \"  Lost: \" & (145 - num";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombre+"  Lost: "+BA.NumberToString((145-_numrowreal))));
 };
 }else {
 //BA.debugLineNum = 1194;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1196;BA.debugLine="Label5.Text = Nombre & \" Error Row \" & numrow";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombre+" Error Row "+BA.NumberToString(_numrow)));
 //BA.debugLineNum = 1197;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1199;BA.debugLine="Return(True)";
if (true) return (anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1203;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1204;BA.debugLine="Label5.Text = Nombre & \"  not found. \"";
mostCurrent._label5.setText(BA.ObjectToCharSequence(_nombre+"  not found. "));
 //BA.debugLineNum = 1205;BA.debugLine="Return(False)";
if (true) return (anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1208;BA.debugLine="End Sub";
return false;
}
public static void  _activity_create(boolean _firsttime) throws Exception{
ResumableSub_Activity_Create rsub = new ResumableSub_Activity_Create(null,_firsttime);
rsub.resume(processBA, null);
}
public static class ResumableSub_Activity_Create extends BA.ResumableSub {
public ResumableSub_Activity_Create(b4a.tessp.main parent,boolean _firsttime) {
this.parent = parent;
this._firsttime = _firsttime;
}
b4a.tessp.main parent;
boolean _firsttime;
String _permission = "";
boolean _result = false;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 256;BA.debugLine="Activity.LoadLayout(\"LayoutAndro2BLE\")";
parent.mostCurrent._activity.LoadLayout("LayoutAndro2BLE",mostCurrent.activityBA);
 //BA.debugLineNum = 257;BA.debugLine="Activity.Title = \"TESS P\"";
parent.mostCurrent._activity.setTitle(BA.ObjectToCharSequence("TESS P"));
 //BA.debugLineNum = 258;BA.debugLine="Activity.TitleColor = Colors.Black";
parent.mostCurrent._activity.setTitleColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 260;BA.debugLine="Label11.Text = \"TESS P\"";
parent.mostCurrent._label11.setText(BA.ObjectToCharSequence("TESS P"));
 //BA.debugLineNum = 261;BA.debugLine="Lversion.Text = Application.VersionName";
parent.mostCurrent._lversion.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getVersionName()));
 //BA.debugLineNum = 262;BA.debugLine="If FirstTime Then";
if (true) break;

case 1:
//if
this.state = 10;
if (_firsttime) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 263;BA.debugLine="Starter.CuentaSend = 0";
parent.mostCurrent._starter._cuentasend /*int*/  = (int) (0);
 //BA.debugLineNum = 264;BA.debugLine="mqtt.Initialize(\"mqtt\",  \"tcp://\"& mqtt_server_l";
parent._mqtt.Initialize(processBA,"mqtt","tcp://"+parent._mqtt_server_local+":1883","B4X001");
 //BA.debugLineNum = 265;BA.debugLine="Tim1s.Initialize(\"Tim1s\",500)";
parent._tim1s.Initialize(processBA,"Tim1s",(long) (500));
 //BA.debugLineNum = 266;BA.debugLine="TimBoton.Initialize(\"TimBoton\",500)";
parent._timboton.Initialize(processBA,"TimBoton",(long) (500));
 //BA.debugLineNum = 267;BA.debugLine="Label5.Text =  \" Log.\"";
parent.mostCurrent._label5.setText(BA.ObjectToCharSequence(" Log."));
 //BA.debugLineNum = 268;BA.debugLine="Bmqtt.Visible = False";
parent.mostCurrent._bmqtt.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 270;BA.debugLine="Linform.Visible = True";
parent.mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 289;BA.debugLine="rpcfg.CheckAndRequest(rpcfg.PERMISSION_ACCESS_FI";
parent._rpcfg.CheckAndRequest(processBA,parent._rpcfg.PERMISSION_ACCESS_FINE_LOCATION);
 //BA.debugLineNum = 290;BA.debugLine="Wait For Activity_PermissionResult (Permission A";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 29;
return;
case 29:
//C
this.state = 4;
_permission = (String) result[0];
_result = (Boolean) result[1];
;
 //BA.debugLineNum = 291;BA.debugLine="If Result Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_result) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 292;BA.debugLine="Starter.gps_permiso_ok = True";
parent.mostCurrent._starter._gps_permiso_ok /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 8:
//C
this.state = 9;
 if (true) break;

case 9:
//C
this.state = 10;
;
 //BA.debugLineNum = 298;BA.debugLine="Starter.TiempoSalvarSg = 10";
parent.mostCurrent._starter._tiemposalvarsg /*int*/  = (int) (10);
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 301;BA.debugLine="Activity.Color = Colors.Black";
parent.mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 302;BA.debugLine="Elugar.TextColor =Colors.RGB(0x7F,0x9D,0xF9)";
parent.mostCurrent._elugar.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 303;BA.debugLine="Bmqtt.Color = Colors.RGB(0x1F,0x1F,0x1F)'Colors.D";
parent.mostCurrent._bmqtt.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x1f),(int) (0x1f),(int) (0x1f)));
 //BA.debugLineNum = 304;BA.debugLine="Bgraph.Color = Colors.DarkGray";
parent.mostCurrent._bgraph.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 305;BA.debugLine="BAlarmaIR.Color = Colors.DarkGray";
parent.mostCurrent._balarmair.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 306;BA.debugLine="BAlarmaMV.Color = Colors.DarkGray";
parent.mostCurrent._balarmamv.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 307;BA.debugLine="Button1.Color = Colors.Black";
parent.mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 310;BA.debugLine="Label3.Text = \"-\"";
parent.mostCurrent._label3.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 311;BA.debugLine="LtempIR.Text = \"-\"";
parent.mostCurrent._ltempir.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 312;BA.debugLine="Ltsensor.Text = \"-\"";
parent.mostCurrent._ltsensor.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 317;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"\"";
if (true) break;

case 11:
//if
this.state = 28;
if (anywheresoftware.b4a.keywords.Common.File.Exists(parent._rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 13;
}else {
this.state = 21;
}if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 319;BA.debugLine="ToastMessageShow(\"First run. Enter a sensor name";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("First run. Enter a sensor name."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 321;BA.debugLine="Try";
if (true) break;

case 14:
//try
this.state = 19;
this.catchState = 18;
this.state = 16;
if (true) break;

case 16:
//C
this.state = 19;
this.catchState = 18;
 //BA.debugLineNum = 322;BA.debugLine="File.WriteString(rpcfg.GetSafeDirDefaultExterna";
anywheresoftware.b4a.keywords.Common.File.WriteString(parent._rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt","starsX");
 if (true) break;

case 18:
//C
this.state = 19;
this.catchState = 0;
 //BA.debugLineNum = 324;BA.debugLine="ToastMessageShow(\"First run. Can't write cfg fi";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("First run. Can't write cfg file."),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;
if (true) break;

case 19:
//C
this.state = 28;
this.catchState = 0;
;
 //BA.debugLineNum = 326;BA.debugLine="Starter.NameSensor = \"starsX\"";
parent.mostCurrent._starter._namesensor /*String*/  = "starsX";
 if (true) break;

case 21:
//C
this.state = 22;
 //BA.debugLineNum = 328;BA.debugLine="Starter.NameSensor = File.ReadString( rpcfg.GetS";
parent.mostCurrent._starter._namesensor /*String*/  = anywheresoftware.b4a.keywords.Common.File.ReadString(parent._rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt");
 //BA.debugLineNum = 330;BA.debugLine="EditText1.Text = Starter.NameSensor";
parent.mostCurrent._edittext1.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._namesensor /*String*/ ));
 //BA.debugLineNum = 331;BA.debugLine="EditText1.TextColor =Colors.RGB(0x7F,0x9D,0xF9)";
parent.mostCurrent._edittext1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 332;BA.debugLine="If Starter.NameSensor.StartsWith(\"TAS\") Then";
if (true) break;

case 22:
//if
this.state = 27;
if (parent.mostCurrent._starter._namesensor /*String*/ .startsWith("TAS")) { 
this.state = 24;
}else {
this.state = 26;
}if (true) break;

case 24:
//C
this.state = 27;
 //BA.debugLineNum = 333;BA.debugLine="BstartTas.Enabled = True";
parent.mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 334;BA.debugLine="BstartTas.TextColor = Colors.Green";
parent.mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 335;BA.debugLine="BstartTas.Color = Colors.DarkGray";
parent.mostCurrent._bstarttas.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 if (true) break;

case 26:
//C
this.state = 27;
 //BA.debugLineNum = 337;BA.debugLine="BstartTas.Enabled = False";
parent.mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 338;BA.debugLine="BstartTas.Color = Colors.DarkGray";
parent.mostCurrent._bstarttas.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 339;BA.debugLine="BstartTas.TextColor = Colors.Gray";
parent.mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 if (true) break;

case 27:
//C
this.state = 28;
;
 if (true) break;

case 28:
//C
this.state = -1;
;
 //BA.debugLineNum = 344;BA.debugLine="DateTime.DateFormat = \"yyyy-MM-dd\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("yyyy-MM-dd");
 //BA.debugLineNum = 347;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 429;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 430;BA.debugLine="Tim1s.Enabled=True";
_tim1s.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 432;BA.debugLine="TBtiempo.TextOn = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOn(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 433;BA.debugLine="TBtiempo.TextOff = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOff(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 435;BA.debugLine="End Sub";
return "";
}
public static String  _activity_permissionresult(String _permission,boolean _result) throws Exception{
 //BA.debugLineNum = 438;BA.debugLine="Sub Activity_PermissionResult (Permission As Strin";
 //BA.debugLineNum = 440;BA.debugLine="If Permission = rpcfg.PERMISSION_ACCESS_COARSE_LO";
if ((_permission).equals(_rpcfg.PERMISSION_ACCESS_COARSE_LOCATION)) { 
 //BA.debugLineNum = 442;BA.debugLine="If Result Then";
if (_result) { 
 //BA.debugLineNum = 443;BA.debugLine="CallSub(Starter, \"StartGPS\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"StartGPS");
 };
 };
 //BA.debugLineNum = 447;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 350;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 351;BA.debugLine="EditText1.TextColor = Colors.RGB(0x7F,0x9D,0xF9)";
mostCurrent._edittext1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 352;BA.debugLine="Label9.TextColor = Colors.LightGray";
mostCurrent._label9.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 353;BA.debugLine="Label6.TextColor = Colors.LightGray";
mostCurrent._label6.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 354;BA.debugLine="Label1.TextColor = Colors.LightGray";
mostCurrent._label1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 355;BA.debugLine="Lorientacion.TextColor = Colors.LightGray";
mostCurrent._lorientacion.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 356;BA.debugLine="Label3.TextColor = Colors.Green";
mostCurrent._label3.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 357;BA.debugLine="Label5.TextColor = Colors.LightGray";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 358;BA.debugLine="Lversion.TextColor = Colors.LightGray";
mostCurrent._lversion.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 359;BA.debugLine="Lnelm.TextColor = Colors.LightGray";
mostCurrent._lnelm.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 360;BA.debugLine="TBtiempo.Color = Colors.RGB(0x1F,0x1F,0x1F)'Color";
mostCurrent._tbtiempo.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x1f),(int) (0x1f),(int) (0x1f)));
 //BA.debugLineNum = 361;BA.debugLine="CBsave.TextColor = Colors.Green";
mostCurrent._cbsave.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 362;BA.debugLine="CBsave.Color = Colors.Black";
mostCurrent._cbsave.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 363;BA.debugLine="CBcolor.TextColor = Colors.Green";
mostCurrent._cbcolor.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 364;BA.debugLine="CBcolor.Color = Colors.DarkGray";
mostCurrent._cbcolor.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 366;BA.debugLine="Linform.TextColor = Colors.LightGray";
mostCurrent._linform.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 368;BA.debugLine="CBtir.TextColor = Colors.Green";
mostCurrent._cbtir.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 369;BA.debugLine="CBtir.Color = Colors.DarkGray";
mostCurrent._cbtir.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 371;BA.debugLine="LtempIR.TextColor = Colors.Red";
mostCurrent._ltempir.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 372;BA.debugLine="Ltsensor.TextColor = Colors.RGB(0x2F,0x60,0xF7) '";
mostCurrent._ltsensor.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x2f),(int) (0x60),(int) (0xf7)));
 //BA.debugLineNum = 373;BA.debugLine="CBsave.Color = Colors.RGB(0x1F,0x1F,0x1F)' Colors";
mostCurrent._cbsave.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x1f),(int) (0x1f),(int) (0x1f)));
 //BA.debugLineNum = 375;BA.debugLine="If Starter.EstadoCBsabe = True Then '";
if (mostCurrent._starter._estadocbsabe /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 376;BA.debugLine="CBsave.Checked = True";
mostCurrent._cbsave.setChecked(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 378;BA.debugLine="CBsave.Checked = False";
mostCurrent._cbsave.setChecked(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 381;BA.debugLine="If Starter.FlagAlarmMV = True Then";
if (mostCurrent._starter._flagalarmmv /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 382;BA.debugLine="BAlarmaMV.TextColor = Colors.Green";
mostCurrent._balarmamv.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 }else {
 //BA.debugLineNum = 384;BA.debugLine="BAlarmaMV.TextColor = Colors.LightGray";
mostCurrent._balarmamv.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 };
 //BA.debugLineNum = 386;BA.debugLine="If Starter.FlagAlarmIR = True Then";
if (mostCurrent._starter._flagalarmir /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 387;BA.debugLine="BAlarmaIR.TextColor = Colors.Green";
mostCurrent._balarmair.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 }else {
 //BA.debugLineNum = 389;BA.debugLine="BAlarmaIR.TextColor = Colors.LightGray";
mostCurrent._balarmair.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 };
 //BA.debugLineNum = 392;BA.debugLine="If Starter.Magnitud = 24 Then";
if (mostCurrent._starter._magnitud /*float*/ ==24) { 
 };
 //BA.debugLineNum = 396;BA.debugLine="Tim1s.Enabled=True";
_tim1s.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 398;BA.debugLine="TBtiempo.TextOn = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOn(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 399;BA.debugLine="TBtiempo.TextOff = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOff(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 400;BA.debugLine="btnReadData.Color = Colors.DarkGray";
mostCurrent._btnreaddata.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 401;BA.debugLine="btnReadData.TextColor = Colors.Green";
mostCurrent._btnreaddata.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 402;BA.debugLine="btnDisconnect.Color = Colors.DarkGray";
mostCurrent._btndisconnect.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 403;BA.debugLine="btnDisconnect.TextColor = Colors.Green";
mostCurrent._btndisconnect.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 405;BA.debugLine="Button1.TextColor = Colors.Red";
mostCurrent._button1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 407;BA.debugLine="Bscan.Color = Colors.DarkGray";
mostCurrent._bscan.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 408;BA.debugLine="Bscan.TextColor = Colors.Green";
mostCurrent._bscan.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 410;BA.debugLine="BstartTas.Color = Colors.DarkGray";
mostCurrent._bstarttas.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 412;BA.debugLine="BstartTas.Color = Colors.DarkGray";
mostCurrent._bstarttas.setColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 413;BA.debugLine="If Starter.NameSensor.Contains(\"TAS\") Then";
if (mostCurrent._starter._namesensor /*String*/ .contains("TAS")) { 
 //BA.debugLineNum = 414;BA.debugLine="BstartTas.Enabled = True";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 415;BA.debugLine="BstartTas.TextColor = Colors.Green";
mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 }else {
 //BA.debugLineNum = 417;BA.debugLine="BstartTas.Enabled = False";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 418;BA.debugLine="BstartTas.TextColor = Colors.Gray";
mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 };
 //BA.debugLineNum = 421;BA.debugLine="Elugar.Text =  Starter.Lugar";
mostCurrent._elugar.setText(BA.ObjectToCharSequence(mostCurrent._starter._lugar /*String*/ ));
 //BA.debugLineNum = 422;BA.debugLine="Elugar.TextColor = Colors.RGB(0x7F,0x9D,0xF9)";
mostCurrent._elugar.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 424;BA.debugLine="CallSub(Starter,(\"StateChanged\"))";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),("StateChanged"));
 //BA.debugLineNum = 427;BA.debugLine="End Sub";
return "";
}
public static String  _balarmair_click() throws Exception{
 //BA.debugLineNum = 1890;BA.debugLine="Sub BAlarmaIR_Click";
 //BA.debugLineNum = 1891;BA.debugLine="Starter.AlarmIR = False";
mostCurrent._starter._alarmir /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1892;BA.debugLine="If Starter.FlagAlarmIR = False Then";
if (mostCurrent._starter._flagalarmir /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1893;BA.debugLine="Starter.FlagAlarmIR = True";
mostCurrent._starter._flagalarmir /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1894;BA.debugLine="BAlarmaIR.Text = \"IR ON\"";
mostCurrent._balarmair.setText(BA.ObjectToCharSequence("IR ON"));
 //BA.debugLineNum = 1895;BA.debugLine="BAlarmaIR.TextColor = Colors.Green";
mostCurrent._balarmair.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1896;BA.debugLine="Starter.RefAlarmIR = Starter.TObj";
mostCurrent._starter._refalarmir /*float*/  = mostCurrent._starter._tobj /*float*/ ;
 //BA.debugLineNum = 1898;BA.debugLine="Label5.TextColor = Colors.LightGray";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 1899;BA.debugLine="Label5.Text = \"Acustic Alarm for\" & Round2((Star";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Acustic Alarm for"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2((mostCurrent._starter._tobj /*float*/ -mostCurrent._starter._deltair /*float*/ ),(int) (2)))+" > T IR > "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2((mostCurrent._starter._tobj /*float*/ +mostCurrent._starter._deltair /*float*/ ),(int) (2)))));
 }else {
 //BA.debugLineNum = 1902;BA.debugLine="Starter.FlagAlarmIR = False";
mostCurrent._starter._flagalarmir /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1903;BA.debugLine="BAlarmaIR.Text = \"IR OFF\"";
mostCurrent._balarmair.setText(BA.ObjectToCharSequence("IR OFF"));
 //BA.debugLineNum = 1904;BA.debugLine="BAlarmaIR.TextColor = Colors.LightGray";
mostCurrent._balarmair.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 };
 //BA.debugLineNum = 1906;BA.debugLine="End Sub";
return "";
}
public static String  _balarmamv_click() throws Exception{
 //BA.debugLineNum = 1908;BA.debugLine="Sub BAlarmaMV_Click";
 //BA.debugLineNum = 1909;BA.debugLine="Starter.AlarmMV = False";
mostCurrent._starter._alarmmv /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1910;BA.debugLine="If Starter.FlagAlarmMV = False Then";
if (mostCurrent._starter._flagalarmmv /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1911;BA.debugLine="Starter.FlagAlarmMV = True";
mostCurrent._starter._flagalarmmv /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1912;BA.debugLine="BAlarmaMV.Text = \"Ma ON\"";
mostCurrent._balarmamv.setText(BA.ObjectToCharSequence("Ma ON"));
 //BA.debugLineNum = 1913;BA.debugLine="BAlarmaMV.TextColor = Colors.Green";
mostCurrent._balarmamv.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 1914;BA.debugLine="Starter.RefAlarmMV = Starter.Magnitud";
mostCurrent._starter._refalarmmv /*float*/  = mostCurrent._starter._magnitud /*float*/ ;
 //BA.debugLineNum = 1915;BA.debugLine="Label5.TextColor = Colors.LightGray";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 1916;BA.debugLine="Label5.Text = \"Acustic Alarm for:  \" & Round2((S";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Acustic Alarm for:  "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2((mostCurrent._starter._magnitud /*float*/ -mostCurrent._starter._deltamag /*float*/ ),(int) (2)))+" > Mag > "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2((mostCurrent._starter._magnitud /*float*/ +mostCurrent._starter._deltamag /*float*/ ),(int) (2)))));
 }else {
 //BA.debugLineNum = 1918;BA.debugLine="Starter.FlagAlarmMV = False";
mostCurrent._starter._flagalarmmv /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1919;BA.debugLine="BAlarmaMV.Text = \"Ma OFF\"";
mostCurrent._balarmamv.setText(BA.ObjectToCharSequence("Ma OFF"));
 //BA.debugLineNum = 1920;BA.debugLine="BAlarmaMV.TextColor = Colors.LightGray";
mostCurrent._balarmamv.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 };
 //BA.debugLineNum = 1923;BA.debugLine="End Sub";
return "";
}
public static String  _bgraph_click() throws Exception{
anywheresoftware.b4a.objects.collections.List _listafiles = null;
int _i = 0;
int _c = 0;
int _d = 0;
String _fichero = "";
 //BA.debugLineNum = 884;BA.debugLine="Sub Bgraph_Click";
 //BA.debugLineNum = 886;BA.debugLine="Dim listafiles As List";
_listafiles = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 887;BA.debugLine="Dim i,c,d As Int";
_i = 0;
_c = 0;
_d = 0;
 //BA.debugLineNum = 888;BA.debugLine="Dim fichero As String";
_fichero = "";
 //BA.debugLineNum = 890;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 891;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 893;BA.debugLine="If ListView1.Visible = False Then";
if (mostCurrent._listview1.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 894;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 896;BA.debugLine="ListView1.Visible = False";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 899;BA.debugLine="ListView1.SingleLineLayout.Label.TextSize = 14";
mostCurrent._listview1.getSingleLineLayout().Label.setTextSize((float) (14));
 //BA.debugLineNum = 900;BA.debugLine="ListView1.FastScrollEnabled = True";
mostCurrent._listview1.setFastScrollEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 901;BA.debugLine="ListView1.SingleLineLayout.ItemHeight = 60";
mostCurrent._listview1.getSingleLineLayout().setItemHeight((int) (60));
 //BA.debugLineNum = 902;BA.debugLine="ListView1.SingleLineLayout.Label.TextColor = Colo";
mostCurrent._listview1.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 906;BA.debugLine="listafiles = File.ListFiles(rpcfg.GetSafeDirDefau";
_listafiles = anywheresoftware.b4a.keywords.Common.File.ListFiles(_rpcfg.GetSafeDirDefaultExternal(""));
 //BA.debugLineNum = 907;BA.debugLine="listafiles.Sort(True)";
_listafiles.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 908;BA.debugLine="For i = 0 To  listafiles.Size -1";
{
final int step17 = 1;
final int limit17 = (int) (_listafiles.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit17 ;_i = _i + step17 ) {
 //BA.debugLineNum = 909;BA.debugLine="fichero =  	listafiles.Get(i)";
_fichero = BA.ObjectToString(_listafiles.Get(_i));
 //BA.debugLineNum = 910;BA.debugLine="d = d+1";
_d = (int) (_d+1);
 //BA.debugLineNum = 911;BA.debugLine="If  fichero.Contains(\"txt\") Then";
if (_fichero.contains("txt")) { 
 //BA.debugLineNum = 912;BA.debugLine="If fichero.Contains(\"TSP\") Or fichero.Contains(";
if (_fichero.contains("TSP") || _fichero.contains("TAS")) { 
 //BA.debugLineNum = 913;BA.debugLine="ListView1.AddSingleLine(listafiles.Get(i) )";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence(_listafiles.Get(_i)));
 //BA.debugLineNum = 914;BA.debugLine="c = c + 1";
_c = (int) (_c+1);
 };
 };
 }
};
 //BA.debugLineNum = 921;BA.debugLine="End Sub";
return "";
}
public static void  _bmqtt_click() throws Exception{
ResumableSub_Bmqtt_Click rsub = new ResumableSub_Bmqtt_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_Bmqtt_Click extends BA.ResumableSub {
public ResumableSub_Bmqtt_Click(b4a.tessp.main parent) {
this.parent = parent;
}
b4a.tessp.main parent;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jsongenerator = null;
String _strjson = "";
boolean _success = false;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 733;BA.debugLine="Dim JSONGenerator  As JSONGenerator";
_jsongenerator = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 734;BA.debugLine="Dim strjson As String";
_strjson = "";
 //BA.debugLineNum = 736;BA.debugLine="If Starter.EsperaMsg > 10 Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent.mostCurrent._starter._esperamsg /*int*/ >10) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 737;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 739;BA.debugLine="Starter.espera = 0";
parent.mostCurrent._starter._espera /*int*/  = (int) (0);
 //BA.debugLineNum = 741;BA.debugLine="Bmqtt.TextColor = Colors.Green";
parent.mostCurrent._bmqtt.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 743;BA.debugLine="If Starter.DatoBle Then";
if (true) break;

case 5:
//if
this.state = 10;
if (parent.mostCurrent._starter._datoble /*boolean*/ ) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 10;
 //BA.debugLineNum = 744;BA.debugLine="JSONGenerator.Initialize(Starter.MapBT)";
_jsongenerator.Initialize(parent.mostCurrent._starter._mapbt /*anywheresoftware.b4a.objects.collections.Map*/ );
 if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 746;BA.debugLine="JSONGenerator.Initialize(Starter.Map1)";
_jsongenerator.Initialize(parent.mostCurrent._starter._map1 /*anywheresoftware.b4a.objects.collections.Map*/ );
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 750;BA.debugLine="strjson = JSONGenerator.ToString";
_strjson = _jsongenerator.ToString();
 //BA.debugLineNum = 753;BA.debugLine="If mqconectado = False Then";
if (true) break;

case 11:
//if
this.state = 26;
if (parent._mqconectado==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 13;
}if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 754;BA.debugLine="If myconmq.IsInitialized Then";
if (true) break;

case 14:
//if
this.state = 19;
if (parent._myconmq.IsInitialized()) { 
this.state = 16;
}else {
this.state = 18;
}if (true) break;

case 16:
//C
this.state = 19;
 if (true) break;

case 18:
//C
this.state = 19;
 //BA.debugLineNum = 757;BA.debugLine="myconmq.Initialize(mqtt_user_local, mqtt_passwo";
parent._myconmq.Initialize(parent._mqtt_user_local,parent._mqtt_password_local);
 if (true) break;

case 19:
//C
this.state = 20;
;
 //BA.debugLineNum = 759;BA.debugLine="mqtt.Connect2(myconmq)";
parent._mqtt.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(parent._myconmq.getObject()));
 //BA.debugLineNum = 760;BA.debugLine="wait for Mqtt_Connected (Success As Boolean)";
anywheresoftware.b4a.keywords.Common.WaitFor("mqtt_connected", processBA, this, null);
this.state = 30;
return;
case 30:
//C
this.state = 20;
_success = (Boolean) result[0];
;
 //BA.debugLineNum = 761;BA.debugLine="If Success Then";
if (true) break;

case 20:
//if
this.state = 25;
if (_success) { 
this.state = 22;
}else {
this.state = 24;
}if (true) break;

case 22:
//C
this.state = 25;
 //BA.debugLineNum = 762;BA.debugLine="TimBoton.Enabled = True";
parent._timboton.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 763;BA.debugLine="Label1.Text = \"MQTT connection OK\"";
parent.mostCurrent._label1.setText(BA.ObjectToCharSequence("MQTT connection OK"));
 //BA.debugLineNum = 764;BA.debugLine="mqconectado = True";
parent._mqconectado = anywheresoftware.b4a.keywords.Common.True;
 if (true) break;

case 24:
//C
this.state = 25;
 //BA.debugLineNum = 766;BA.debugLine="Bmqtt.TextColor = Colors.Red";
parent.mostCurrent._bmqtt.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 767;BA.debugLine="TimBoton.Enabled = True";
parent._timboton.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 768;BA.debugLine="Label1.Text =  \"Send FAIL to \" & mqtt_server_lo";
parent.mostCurrent._label1.setText(BA.ObjectToCharSequence("Send FAIL to "+parent._mqtt_server_local));
 if (true) break;

case 25:
//C
this.state = 26;
;
 if (true) break;
;
 //BA.debugLineNum = 772;BA.debugLine="If (mqconectado = True) Then";

case 26:
//if
this.state = 29;
if ((parent._mqconectado==anywheresoftware.b4a.keywords.Common.True)) { 
this.state = 28;
}if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 774;BA.debugLine="my_topic = my_topic.Replace(\"pruebaj\", Starter.N";
parent._my_topic = parent._my_topic.replace("pruebaj",parent.mostCurrent._starter._namesensor /*String*/ );
 //BA.debugLineNum = 776;BA.debugLine="Starter.CuentaSend = Starter.CuentaSend + 1";
parent.mostCurrent._starter._cuentasend /*int*/  = (int) (parent.mostCurrent._starter._cuentasend /*int*/ +1);
 //BA.debugLineNum = 778;BA.debugLine="mqtt.Publish(  my_topic,  strjson.GetBytes(\"UTF8";
parent._mqtt.Publish(parent._my_topic,_strjson.getBytes("UTF8"));
 //BA.debugLineNum = 779;BA.debugLine="Label5.Text = DateTime.Time(DateTime.Now) &  \" S";
parent.mostCurrent._label5.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" Send OK to "+parent._mqtt_server_local));
 //BA.debugLineNum = 780;BA.debugLine="Starter.espera = 0";
parent.mostCurrent._starter._espera /*int*/  = (int) (0);
 //BA.debugLineNum = 781;BA.debugLine="TimBoton.Enabled = True";
parent._timboton.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 29:
//C
this.state = -1;
;
 //BA.debugLineNum = 784;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _mqtt_connected(boolean _success) throws Exception{
}
public static String  _bscan_click() throws Exception{
 //BA.debugLineNum = 2033;BA.debugLine="Sub Bscan_Click";
 //BA.debugLineNum = 2041;BA.debugLine="End Sub";
return "";
}
public static String  _bstarttas_click() throws Exception{
 //BA.debugLineNum = 2060;BA.debugLine="Sub BstartTas_Click";
 //BA.debugLineNum = 2061;BA.debugLine="ToastMessageShow(\"Long Click for START/STOP\", Fal";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Long Click for START/STOP"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2062;BA.debugLine="End Sub";
return "";
}
public static String  _bstarttas_longclick() throws Exception{
byte[] _kk = null;
 //BA.debugLineNum = 2064;BA.debugLine="Sub BstartTas_LongClick";
 //BA.debugLineNum = 2065;BA.debugLine="Dim kk(5) As Byte";
_kk = new byte[(int) (5)];
;
 //BA.debugLineNum = 2067;BA.debugLine="Starter.SegScan = 0";
mostCurrent._starter._segscan /*int*/  = (int) (0);
 //BA.debugLineNum = 2069;BA.debugLine="If Starter.NameSensor.Contains(\"TAS\") Then";
if (mostCurrent._starter._namesensor /*String*/ .contains("TAS")) { 
 //BA.debugLineNum = 2070;BA.debugLine="If Starter.salvandoScan = False Then";
if (mostCurrent._starter._salvandoscan /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 2071;BA.debugLine="Log(\"Start Scan\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62293767","Start Scan",0);
 //BA.debugLineNum = 2072;BA.debugLine="BstartTas.TextColor = Colors.Gray";
mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 //BA.debugLineNum = 2073;BA.debugLine="BstartTas.Enabled = False";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2074;BA.debugLine="Starter.bep.Beep";
mostCurrent._starter._bep /*anywheresoftware.b4a.audio.Beeper*/ .Beep();
 //BA.debugLineNum = 2075;BA.debugLine="kk(0) = 0x53  'S   para Start Scan";
_kk[(int) (0)] = (byte) (0x53);
 //BA.debugLineNum = 2076;BA.debugLine="kk(1) = 0x32";
_kk[(int) (1)] = (byte) (0x32);
 //BA.debugLineNum = 2077;BA.debugLine="kk(2) = 0x33";
_kk[(int) (2)] = (byte) (0x33);
 //BA.debugLineNum = 2078;BA.debugLine="kk(3) = 0x34";
_kk[(int) (3)] = (byte) (0x34);
 //BA.debugLineNum = 2080;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2081;BA.debugLine="ListView1.Visible = False";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 2083;BA.debugLine="Starter.bep.Beep";
mostCurrent._starter._bep /*anywheresoftware.b4a.audio.Beeper*/ .Beep();
 //BA.debugLineNum = 2084;BA.debugLine="Starter.salvandoScan = False";
mostCurrent._starter._salvandoscan /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 2085;BA.debugLine="Log(\"stop\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62293781","stop",0);
 //BA.debugLineNum = 2087;BA.debugLine="kk(0) = 0x54  'T   para Stop Scan";
_kk[(int) (0)] = (byte) (0x54);
 //BA.debugLineNum = 2088;BA.debugLine="kk(1) = 0x32";
_kk[(int) (1)] = (byte) (0x32);
 //BA.debugLineNum = 2089;BA.debugLine="kk(2) = 0x33";
_kk[(int) (2)] = (byte) (0x33);
 //BA.debugLineNum = 2090;BA.debugLine="kk(3) = 0x34";
_kk[(int) (3)] = (byte) (0x34);
 };
 //BA.debugLineNum = 2092;BA.debugLine="Starter.Manager.WriteData(Starter.SERVICE_UUID,";
mostCurrent._starter._manager /*anywheresoftware.b4a.objects.BleManager2*/ .WriteData(mostCurrent._starter._service_uuid /*String*/ ,mostCurrent._starter._characteristic_uuid_tx /*String*/ ,_kk);
 };
 //BA.debugLineNum = 2094;BA.debugLine="End Sub";
return "";
}
public static String  _btndisconnect_click() throws Exception{
 //BA.debugLineNum = 2043;BA.debugLine="Sub btnDisconnect_Click";
 //BA.debugLineNum = 2044;BA.debugLine="CallSub(Starter, \"Disconnect\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"Disconnect");
 //BA.debugLineNum = 2045;BA.debugLine="Starter.arrancado = False";
mostCurrent._starter._arrancado /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 2048;BA.debugLine="Starter.EsperaMsg = 0";
mostCurrent._starter._esperamsg /*int*/  = (int) (0);
 //BA.debugLineNum = 2049;BA.debugLine="End Sub";
return "";
}
public static String  _btnreaddata_click() throws Exception{
 //BA.debugLineNum = 2053;BA.debugLine="Sub btnReadData_Click";
 //BA.debugLineNum = 2056;BA.debugLine="CallSub(Starter, \"ReadData\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"ReadData");
 //BA.debugLineNum = 2058;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 2101;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 2102;BA.debugLine="ToastMessageShow(\"Long Click EXIT app.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Long Click EXIT app."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 2103;BA.debugLine="End Sub";
return "";
}
public static String  _button1_longclick() throws Exception{
 //BA.debugLineNum = 2096;BA.debugLine="Sub Button1_LongClick";
 //BA.debugLineNum = 2097;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 2098;BA.debugLine="End Sub";
return "";
}
public static String  _bz() throws Exception{
float _ma = 0f;
String _nombre_fichero = "";
int _nlinea = 0;
int[] _naltura = null;
int[] _nazimuth = null;
anywheresoftware.b4a.objects.StringUtils _su = null;
anywheresoftware.b4a.objects.collections.List _table = null;
String[] _row = null;
 //BA.debugLineNum = 1365;BA.debugLine="Private Sub bz	' Del fichero intermedio extrae tab";
 //BA.debugLineNum = 1369;BA.debugLine="Private ma As Float";
_ma = 0f;
 //BA.debugLineNum = 1370;BA.debugLine="Private nombre_fichero As String";
_nombre_fichero = "";
 //BA.debugLineNum = 1374;BA.debugLine="Private nlinea As Int";
_nlinea = 0;
 //BA.debugLineNum = 1375;BA.debugLine="Private naltura(48), nazimuth(48) As Int";
_naltura = new int[(int) (48)];
;
_nazimuth = new int[(int) (48)];
;
 //BA.debugLineNum = 1379;BA.debugLine="naltura(0) = 0";
_naltura[(int) (0)] = (int) (0);
 //BA.debugLineNum = 1380;BA.debugLine="naltura(1) = 1";
_naltura[(int) (1)] = (int) (1);
 //BA.debugLineNum = 1381;BA.debugLine="naltura(2) = 2";
_naltura[(int) (2)] = (int) (2);
 //BA.debugLineNum = 1382;BA.debugLine="naltura(3) = 3";
_naltura[(int) (3)] = (int) (3);
 //BA.debugLineNum = 1383;BA.debugLine="naltura(4) = 3";
_naltura[(int) (4)] = (int) (3);
 //BA.debugLineNum = 1384;BA.debugLine="naltura(5) = 2";
_naltura[(int) (5)] = (int) (2);
 //BA.debugLineNum = 1385;BA.debugLine="naltura(6) = 1";
_naltura[(int) (6)] = (int) (1);
 //BA.debugLineNum = 1386;BA.debugLine="naltura(7) = 0";
_naltura[(int) (7)] = (int) (0);
 //BA.debugLineNum = 1388;BA.debugLine="naltura(8) = 0";
_naltura[(int) (8)] = (int) (0);
 //BA.debugLineNum = 1389;BA.debugLine="naltura(9) = 1";
_naltura[(int) (9)] = (int) (1);
 //BA.debugLineNum = 1390;BA.debugLine="naltura(10) = 2";
_naltura[(int) (10)] = (int) (2);
 //BA.debugLineNum = 1391;BA.debugLine="naltura(11) = 3";
_naltura[(int) (11)] = (int) (3);
 //BA.debugLineNum = 1392;BA.debugLine="naltura(12) = 3";
_naltura[(int) (12)] = (int) (3);
 //BA.debugLineNum = 1393;BA.debugLine="naltura(13) = 2";
_naltura[(int) (13)] = (int) (2);
 //BA.debugLineNum = 1394;BA.debugLine="naltura(14) = 1";
_naltura[(int) (14)] = (int) (1);
 //BA.debugLineNum = 1395;BA.debugLine="naltura(15) = 0";
_naltura[(int) (15)] = (int) (0);
 //BA.debugLineNum = 1397;BA.debugLine="naltura(16) = 0";
_naltura[(int) (16)] = (int) (0);
 //BA.debugLineNum = 1398;BA.debugLine="naltura(17) = 1";
_naltura[(int) (17)] = (int) (1);
 //BA.debugLineNum = 1399;BA.debugLine="naltura(18) = 2";
_naltura[(int) (18)] = (int) (2);
 //BA.debugLineNum = 1400;BA.debugLine="naltura(19) = 3";
_naltura[(int) (19)] = (int) (3);
 //BA.debugLineNum = 1401;BA.debugLine="naltura(20) = 3";
_naltura[(int) (20)] = (int) (3);
 //BA.debugLineNum = 1402;BA.debugLine="naltura(21) = 2";
_naltura[(int) (21)] = (int) (2);
 //BA.debugLineNum = 1403;BA.debugLine="naltura(22) = 1";
_naltura[(int) (22)] = (int) (1);
 //BA.debugLineNum = 1404;BA.debugLine="naltura(23) = 0";
_naltura[(int) (23)] = (int) (0);
 //BA.debugLineNum = 1406;BA.debugLine="naltura(24) = 0";
_naltura[(int) (24)] = (int) (0);
 //BA.debugLineNum = 1407;BA.debugLine="naltura(25) = 1";
_naltura[(int) (25)] = (int) (1);
 //BA.debugLineNum = 1408;BA.debugLine="naltura(26) = 2";
_naltura[(int) (26)] = (int) (2);
 //BA.debugLineNum = 1409;BA.debugLine="naltura(27) = 3";
_naltura[(int) (27)] = (int) (3);
 //BA.debugLineNum = 1410;BA.debugLine="naltura(28) = 3";
_naltura[(int) (28)] = (int) (3);
 //BA.debugLineNum = 1411;BA.debugLine="naltura(29) = 2";
_naltura[(int) (29)] = (int) (2);
 //BA.debugLineNum = 1412;BA.debugLine="naltura(30) = 1";
_naltura[(int) (30)] = (int) (1);
 //BA.debugLineNum = 1413;BA.debugLine="naltura(31) = 0";
_naltura[(int) (31)] = (int) (0);
 //BA.debugLineNum = 1415;BA.debugLine="naltura(32) = 0";
_naltura[(int) (32)] = (int) (0);
 //BA.debugLineNum = 1416;BA.debugLine="naltura(33) = 1";
_naltura[(int) (33)] = (int) (1);
 //BA.debugLineNum = 1417;BA.debugLine="naltura(34) = 2";
_naltura[(int) (34)] = (int) (2);
 //BA.debugLineNum = 1418;BA.debugLine="naltura(35) = 3";
_naltura[(int) (35)] = (int) (3);
 //BA.debugLineNum = 1419;BA.debugLine="naltura(36) = 3";
_naltura[(int) (36)] = (int) (3);
 //BA.debugLineNum = 1420;BA.debugLine="naltura(37) = 2";
_naltura[(int) (37)] = (int) (2);
 //BA.debugLineNum = 1421;BA.debugLine="naltura(38) = 1";
_naltura[(int) (38)] = (int) (1);
 //BA.debugLineNum = 1422;BA.debugLine="naltura(39) = 0";
_naltura[(int) (39)] = (int) (0);
 //BA.debugLineNum = 1424;BA.debugLine="naltura(40) = 0";
_naltura[(int) (40)] = (int) (0);
 //BA.debugLineNum = 1425;BA.debugLine="naltura(41) = 1";
_naltura[(int) (41)] = (int) (1);
 //BA.debugLineNum = 1426;BA.debugLine="naltura(42) = 2";
_naltura[(int) (42)] = (int) (2);
 //BA.debugLineNum = 1427;BA.debugLine="naltura(43) = 3";
_naltura[(int) (43)] = (int) (3);
 //BA.debugLineNum = 1428;BA.debugLine="naltura(44) = 3";
_naltura[(int) (44)] = (int) (3);
 //BA.debugLineNum = 1429;BA.debugLine="naltura(45) = 2";
_naltura[(int) (45)] = (int) (2);
 //BA.debugLineNum = 1430;BA.debugLine="naltura(46) = 1";
_naltura[(int) (46)] = (int) (1);
 //BA.debugLineNum = 1431;BA.debugLine="naltura(47) = 0";
_naltura[(int) (47)] = (int) (0);
 //BA.debugLineNum = 1433;BA.debugLine="nazimuth(0) = 0";
_nazimuth[(int) (0)] = (int) (0);
 //BA.debugLineNum = 1434;BA.debugLine="nazimuth(1) = 0";
_nazimuth[(int) (1)] = (int) (0);
 //BA.debugLineNum = 1435;BA.debugLine="nazimuth(2) = 0";
_nazimuth[(int) (2)] = (int) (0);
 //BA.debugLineNum = 1436;BA.debugLine="nazimuth(3) = 0";
_nazimuth[(int) (3)] = (int) (0);
 //BA.debugLineNum = 1437;BA.debugLine="nazimuth(4) = 6";
_nazimuth[(int) (4)] = (int) (6);
 //BA.debugLineNum = 1438;BA.debugLine="nazimuth(5) = 6";
_nazimuth[(int) (5)] = (int) (6);
 //BA.debugLineNum = 1439;BA.debugLine="nazimuth(6) = 6";
_nazimuth[(int) (6)] = (int) (6);
 //BA.debugLineNum = 1440;BA.debugLine="nazimuth(7) = 6";
_nazimuth[(int) (7)] = (int) (6);
 //BA.debugLineNum = 1442;BA.debugLine="nazimuth(8) = 1";
_nazimuth[(int) (8)] = (int) (1);
 //BA.debugLineNum = 1443;BA.debugLine="nazimuth(9) = 1";
_nazimuth[(int) (9)] = (int) (1);
 //BA.debugLineNum = 1444;BA.debugLine="nazimuth(10) = 1";
_nazimuth[(int) (10)] = (int) (1);
 //BA.debugLineNum = 1445;BA.debugLine="nazimuth(11) = 1";
_nazimuth[(int) (11)] = (int) (1);
 //BA.debugLineNum = 1446;BA.debugLine="nazimuth(12) = 7";
_nazimuth[(int) (12)] = (int) (7);
 //BA.debugLineNum = 1447;BA.debugLine="nazimuth(13) = 7";
_nazimuth[(int) (13)] = (int) (7);
 //BA.debugLineNum = 1448;BA.debugLine="nazimuth(14) = 7";
_nazimuth[(int) (14)] = (int) (7);
 //BA.debugLineNum = 1449;BA.debugLine="nazimuth(15) = 7";
_nazimuth[(int) (15)] = (int) (7);
 //BA.debugLineNum = 1451;BA.debugLine="nazimuth(16) = 2";
_nazimuth[(int) (16)] = (int) (2);
 //BA.debugLineNum = 1452;BA.debugLine="nazimuth(17) = 2";
_nazimuth[(int) (17)] = (int) (2);
 //BA.debugLineNum = 1453;BA.debugLine="nazimuth(18) = 2";
_nazimuth[(int) (18)] = (int) (2);
 //BA.debugLineNum = 1454;BA.debugLine="nazimuth(19) = 2";
_nazimuth[(int) (19)] = (int) (2);
 //BA.debugLineNum = 1455;BA.debugLine="nazimuth(20) = 8";
_nazimuth[(int) (20)] = (int) (8);
 //BA.debugLineNum = 1456;BA.debugLine="nazimuth(21) = 8";
_nazimuth[(int) (21)] = (int) (8);
 //BA.debugLineNum = 1457;BA.debugLine="nazimuth(22) = 8";
_nazimuth[(int) (22)] = (int) (8);
 //BA.debugLineNum = 1458;BA.debugLine="nazimuth(23) = 8";
_nazimuth[(int) (23)] = (int) (8);
 //BA.debugLineNum = 1461;BA.debugLine="nazimuth(24) = 3";
_nazimuth[(int) (24)] = (int) (3);
 //BA.debugLineNum = 1462;BA.debugLine="nazimuth(25) = 3";
_nazimuth[(int) (25)] = (int) (3);
 //BA.debugLineNum = 1463;BA.debugLine="nazimuth(26) = 3";
_nazimuth[(int) (26)] = (int) (3);
 //BA.debugLineNum = 1464;BA.debugLine="nazimuth(27) = 3";
_nazimuth[(int) (27)] = (int) (3);
 //BA.debugLineNum = 1465;BA.debugLine="nazimuth(28) = 9";
_nazimuth[(int) (28)] = (int) (9);
 //BA.debugLineNum = 1466;BA.debugLine="nazimuth(29) = 9";
_nazimuth[(int) (29)] = (int) (9);
 //BA.debugLineNum = 1467;BA.debugLine="nazimuth(30) = 9";
_nazimuth[(int) (30)] = (int) (9);
 //BA.debugLineNum = 1468;BA.debugLine="nazimuth(31) = 9";
_nazimuth[(int) (31)] = (int) (9);
 //BA.debugLineNum = 1470;BA.debugLine="nazimuth(32) = 4";
_nazimuth[(int) (32)] = (int) (4);
 //BA.debugLineNum = 1471;BA.debugLine="nazimuth(33) = 4";
_nazimuth[(int) (33)] = (int) (4);
 //BA.debugLineNum = 1472;BA.debugLine="nazimuth(34) = 4";
_nazimuth[(int) (34)] = (int) (4);
 //BA.debugLineNum = 1473;BA.debugLine="nazimuth(35) = 4";
_nazimuth[(int) (35)] = (int) (4);
 //BA.debugLineNum = 1474;BA.debugLine="nazimuth(36) = 10";
_nazimuth[(int) (36)] = (int) (10);
 //BA.debugLineNum = 1475;BA.debugLine="nazimuth(37) = 10";
_nazimuth[(int) (37)] = (int) (10);
 //BA.debugLineNum = 1476;BA.debugLine="nazimuth(38) = 10";
_nazimuth[(int) (38)] = (int) (10);
 //BA.debugLineNum = 1477;BA.debugLine="nazimuth(39) = 10";
_nazimuth[(int) (39)] = (int) (10);
 //BA.debugLineNum = 1479;BA.debugLine="nazimuth(40) = 5";
_nazimuth[(int) (40)] = (int) (5);
 //BA.debugLineNum = 1480;BA.debugLine="nazimuth(41) = 5";
_nazimuth[(int) (41)] = (int) (5);
 //BA.debugLineNum = 1481;BA.debugLine="nazimuth(42) = 5";
_nazimuth[(int) (42)] = (int) (5);
 //BA.debugLineNum = 1482;BA.debugLine="nazimuth(43) = 5";
_nazimuth[(int) (43)] = (int) (5);
 //BA.debugLineNum = 1483;BA.debugLine="nazimuth(44) = 11";
_nazimuth[(int) (44)] = (int) (11);
 //BA.debugLineNum = 1484;BA.debugLine="nazimuth(45) = 11";
_nazimuth[(int) (45)] = (int) (11);
 //BA.debugLineNum = 1485;BA.debugLine="nazimuth(46) = 11";
_nazimuth[(int) (46)] = (int) (11);
 //BA.debugLineNum = 1486;BA.debugLine="nazimuth(47) = 11";
_nazimuth[(int) (47)] = (int) (11);
 //BA.debugLineNum = 1488;BA.debugLine="nombre_fichero = \"sel.txt\"	'fichero intermedio co";
_nombre_fichero = "sel.txt";
 //BA.debugLineNum = 1490;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),_nombre_fichero)) { 
 //BA.debugLineNum = 1491;BA.debugLine="Dim su As StringUtils";
_su = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 1492;BA.debugLine="Dim Table As List";
_table = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 1494;BA.debugLine="Table = su.LoadCSV(rpcfg.GetSafeDirDefaultExtern";
_table = _su.LoadCSV(_rpcfg.GetSafeDirDefaultExternal(""),_nombre_fichero,anywheresoftware.b4a.keywords.Common.Chr((int) (9)));
 //BA.debugLineNum = 1495;BA.debugLine="nlinea = 0";
_nlinea = (int) (0);
 //BA.debugLineNum = 1497;BA.debugLine="MVmin = 100";
_mvmin = (float) (100);
 //BA.debugLineNum = 1498;BA.debugLine="MVmax = 0";
_mvmax = (float) (0);
 //BA.debugLineNum = 1499;BA.debugLine="For Each Row() As String In Table";
{
final anywheresoftware.b4a.BA.IterableList group109 = _table;
final int groupLen109 = group109.getSize()
;int index109 = 0;
;
for (; index109 < groupLen109;index109++){
_row = (String[])(group109.Get(index109));
 //BA.debugLineNum = 1501;BA.debugLine="ma = Row(5)";
_ma = (float)(Double.parseDouble(_row[(int) (5)]));
 //BA.debugLineNum = 1502;BA.debugLine="If nlinea < 48 Then";
if (_nlinea<48) { 
 //BA.debugLineNum = 1503;BA.debugLine="mag2(naltura(nlinea), nazimuth(nlinea)) = ma";
_mag2[_naltura[_nlinea]][_nazimuth[_nlinea]] = _ma;
 //BA.debugLineNum = 1504;BA.debugLine="nlinea = nlinea + 1";
_nlinea = (int) (_nlinea+1);
 //BA.debugLineNum = 1505;BA.debugLine="If ma < MVmin Then";
if (_ma<_mvmin) { 
 //BA.debugLineNum = 1506;BA.debugLine="MVmin = ma";
_mvmin = _ma;
 }else if(_ma>_mvmax) { 
 //BA.debugLineNum = 1508;BA.debugLine="MVmax = ma";
_mvmax = _ma;
 };
 };
 }
};
 }else {
 //BA.debugLineNum = 1515;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 1518;BA.debugLine="If nlinea > 47 Then		' terminada tabla se pinta";
if (_nlinea>47) { 
 //BA.debugLineNum = 1519;BA.debugLine="Brillomax = MVmax";
_brillomax = _mvmax;
 //BA.debugLineNum = 1520;BA.debugLine="Brillomin = MVmin";
_brillomin = _mvmin;
 //BA.debugLineNum = 1532;BA.debugLine="PreparaSectores";
_preparasectores();
 };
 //BA.debugLineNum = 1535;BA.debugLine="End Sub";
return "";
}
public static String  _caldifacimut(int _ac1,int _ac2) throws Exception{
 //BA.debugLineNum = 1212;BA.debugLine="Sub caldifacimut(ac1 As Int, ac2 As Int)";
 //BA.debugLineNum = 1214;BA.debugLine="If ( (ac1 > 350) And (ac2 < 10)) Then";
if (((_ac1>350) && (_ac2<10))) { 
 //BA.debugLineNum = 1215;BA.debugLine="difacimut = (360 - ac1 + ac2)";
_difacimut = (int) ((360-_ac1+_ac2));
 }else if(((_ac1<10) && (_ac2>350))) { 
 //BA.debugLineNum = 1217;BA.debugLine="difacimut =  360 - ac2 + ac1";
_difacimut = (int) (360-_ac2+_ac1);
 }else {
 //BA.debugLineNum = 1219;BA.debugLine="difacimut = Abs(ac1 - ac2)";
_difacimut = (int) (anywheresoftware.b4a.keywords.Common.Abs(_ac1-_ac2));
 };
 //BA.debugLineNum = 1222;BA.debugLine="End Sub";
return "";
}
public static String  _cargapaletanixnox() throws Exception{
 //BA.debugLineNum = 1927;BA.debugLine="Sub CargaPaletaNixNox";
 //BA.debugLineNum = 1928;BA.debugLine="mR(0)= 8";
_mr[(int) (0)] = (int) (8);
 //BA.debugLineNum = 1929;BA.debugLine="mR(1)= 17";
_mr[(int) (1)] = (int) (17);
 //BA.debugLineNum = 1930;BA.debugLine="mR(2)= 25";
_mr[(int) (2)] = (int) (25);
 //BA.debugLineNum = 1931;BA.debugLine="mR(3)= 35";
_mr[(int) (3)] = (int) (35);
 //BA.debugLineNum = 1932;BA.debugLine="mR(4)= 34";
_mr[(int) (4)] = (int) (34);
 //BA.debugLineNum = 1933;BA.debugLine="mR(5)= 32";
_mr[(int) (5)] = (int) (32);
 //BA.debugLineNum = 1934;BA.debugLine="mR(6)= 29";
_mr[(int) (6)] = (int) (29);
 //BA.debugLineNum = 1935;BA.debugLine="mR(7)= 41";
_mr[(int) (7)] = (int) (41);
 //BA.debugLineNum = 1936;BA.debugLine="mR(8)= 17";
_mr[(int) (8)] = (int) (17);
 //BA.debugLineNum = 1937;BA.debugLine="mR(9)= 36";
_mr[(int) (9)] = (int) (36);
 //BA.debugLineNum = 1938;BA.debugLine="mR(10)= 32";
_mr[(int) (10)] = (int) (32);
 //BA.debugLineNum = 1939;BA.debugLine="mR(11)= 25";
_mr[(int) (11)] = (int) (25);
 //BA.debugLineNum = 1940;BA.debugLine="mR(12)= 38";
_mr[(int) (12)] = (int) (38);
 //BA.debugLineNum = 1941;BA.debugLine="mR(13)= 29";
_mr[(int) (13)] = (int) (29);
 //BA.debugLineNum = 1942;BA.debugLine="mR(14)= 39";
_mr[(int) (14)] = (int) (39);
 //BA.debugLineNum = 1943;BA.debugLine="mR(15)= 47";
_mr[(int) (15)] = (int) (47);
 //BA.debugLineNum = 1944;BA.debugLine="mR(16)= 54";
_mr[(int) (16)] = (int) (54);
 //BA.debugLineNum = 1945;BA.debugLine="mR(17)= 75";
_mr[(int) (17)] = (int) (75);
 //BA.debugLineNum = 1946;BA.debugLine="mR(18)= 84";
_mr[(int) (18)] = (int) (84);
 //BA.debugLineNum = 1947;BA.debugLine="mR(19)= 108";
_mr[(int) (19)] = (int) (108);
 //BA.debugLineNum = 1948;BA.debugLine="mR(20)= 128";
_mr[(int) (20)] = (int) (128);
 //BA.debugLineNum = 1949;BA.debugLine="mR(21)= 138";
_mr[(int) (21)] = (int) (138);
 //BA.debugLineNum = 1950;BA.debugLine="mR(22)= 148";
_mr[(int) (22)] = (int) (148);
 //BA.debugLineNum = 1952;BA.debugLine="mR(23)= 160";
_mr[(int) (23)] = (int) (160);
 //BA.debugLineNum = 1953;BA.debugLine="mR(24)= 178";
_mr[(int) (24)] = (int) (178);
 //BA.debugLineNum = 1954;BA.debugLine="mR(25)= 202";
_mr[(int) (25)] = (int) (202);
 //BA.debugLineNum = 1955;BA.debugLine="mR(26)= 214";
_mr[(int) (26)] = (int) (214);
 //BA.debugLineNum = 1956;BA.debugLine="mR(27)= 226";
_mr[(int) (27)] = (int) (226);
 //BA.debugLineNum = 1957;BA.debugLine="mR(28)= 237";
_mr[(int) (28)] = (int) (237);
 //BA.debugLineNum = 1958;BA.debugLine="mR(29)= 242";
_mr[(int) (29)] = (int) (242);
 //BA.debugLineNum = 1959;BA.debugLine="mR(30)= 248";
_mr[(int) (30)] = (int) (248);
 //BA.debugLineNum = 1960;BA.debugLine="mR(31)= 251";
_mr[(int) (31)] = (int) (251);
 //BA.debugLineNum = 1962;BA.debugLine="mG(0)= 31";
_mg[(int) (0)] = (int) (31);
 //BA.debugLineNum = 1963;BA.debugLine="mG(1)= 39";
_mg[(int) (1)] = (int) (39);
 //BA.debugLineNum = 1964;BA.debugLine="mG(2)= 46";
_mg[(int) (2)] = (int) (46);
 //BA.debugLineNum = 1965;BA.debugLine="mG(3)= 51";
_mg[(int) (3)] = (int) (51);
 //BA.debugLineNum = 1966;BA.debugLine="mG(4)= 59";
_mg[(int) (4)] = (int) (59);
 //BA.debugLineNum = 1967;BA.debugLine="mG(5)= 67";
_mg[(int) (5)] = (int) (67);
 //BA.debugLineNum = 1968;BA.debugLine="mG(6)= 77";
_mg[(int) (6)] = (int) (77);
 //BA.debugLineNum = 1969;BA.debugLine="mG(7)= 84";
_mg[(int) (7)] = (int) (84);
 //BA.debugLineNum = 1970;BA.debugLine="mG(8)= 94";
_mg[(int) (8)] = (int) (94);
 //BA.debugLineNum = 1971;BA.debugLine="mG(9)= 104";
_mg[(int) (9)] = (int) (104);
 //BA.debugLineNum = 1972;BA.debugLine="mG(10)= 115";
_mg[(int) (10)] = (int) (115);
 //BA.debugLineNum = 1973;BA.debugLine="mG(11)= 126";
_mg[(int) (11)] = (int) (126);
 //BA.debugLineNum = 1974;BA.debugLine="mG(12)= 135";
_mg[(int) (12)] = (int) (135);
 //BA.debugLineNum = 1975;BA.debugLine="mG(13)= 147";
_mg[(int) (13)] = (int) (147);
 //BA.debugLineNum = 1976;BA.debugLine="mG(14)= 155";
_mg[(int) (14)] = (int) (155);
 //BA.debugLineNum = 1977;BA.debugLine="mG(15)= 163";
_mg[(int) (15)] = (int) (163);
 //BA.debugLineNum = 1978;BA.debugLine="mG(16)= 174";
_mg[(int) (16)] = (int) (174);
 //BA.debugLineNum = 1979;BA.debugLine="mG(17)= 184";
_mg[(int) (17)] = (int) (184);
 //BA.debugLineNum = 1980;BA.debugLine="mG(18)= 193";
_mg[(int) (18)] = (int) (193);
 //BA.debugLineNum = 1981;BA.debugLine="mG(19)= 200";
_mg[(int) (19)] = (int) (200);
 //BA.debugLineNum = 1982;BA.debugLine="mG(20)= 204";
_mg[(int) (20)] = (int) (204);
 //BA.debugLineNum = 1983;BA.debugLine="mG(21)= 210";
_mg[(int) (21)] = (int) (210);
 //BA.debugLineNum = 1984;BA.debugLine="mG(22)= 216";
_mg[(int) (22)] = (int) (216);
 //BA.debugLineNum = 1986;BA.debugLine="mG(23)= 220";
_mg[(int) (23)] = (int) (220);
 //BA.debugLineNum = 1987;BA.debugLine="mG(24)= 225";
_mg[(int) (24)] = (int) (225);
 //BA.debugLineNum = 1988;BA.debugLine="mG(25)= 235";
_mg[(int) (25)] = (int) (235);
 //BA.debugLineNum = 1989;BA.debugLine="mG(26)= 239";
_mg[(int) (26)] = (int) (239);
 //BA.debugLineNum = 1990;BA.debugLine="mG(27)= 242";
_mg[(int) (27)] = (int) (242);
 //BA.debugLineNum = 1991;BA.debugLine="mG(28)= 247";
_mg[(int) (28)] = (int) (247);
 //BA.debugLineNum = 1992;BA.debugLine="mG(29)= 251";
_mg[(int) (29)] = (int) (251);
 //BA.debugLineNum = 1993;BA.debugLine="mG(30)= 251";
_mg[(int) (30)] = (int) (251);
 //BA.debugLineNum = 1994;BA.debugLine="mG(31)= 254";
_mg[(int) (31)] = (int) (254);
 //BA.debugLineNum = 1996;BA.debugLine="mB(0)= 96";
_mb[(int) (0)] = (int) (96);
 //BA.debugLineNum = 1997;BA.debugLine="mB(1)= 113";
_mb[(int) (1)] = (int) (113);
 //BA.debugLineNum = 1998;BA.debugLine="mB(2)= 131";
_mb[(int) (2)] = (int) (131);
 //BA.debugLineNum = 1999;BA.debugLine="mB(3)= 144";
_mb[(int) (3)] = (int) (144);
 //BA.debugLineNum = 2000;BA.debugLine="mB(4)= 151";
_mb[(int) (4)] = (int) (151);
 //BA.debugLineNum = 2001;BA.debugLine="mB(5)= 159";
_mb[(int) (5)] = (int) (159);
 //BA.debugLineNum = 2002;BA.debugLine="mB(6)= 161";
_mb[(int) (6)] = (int) (161);
 //BA.debugLineNum = 2003;BA.debugLine="mB(7)= 163";
_mb[(int) (7)] = (int) (163);
 //BA.debugLineNum = 2004;BA.debugLine="mB(8)= 170";
_mb[(int) (8)] = (int) (170);
 //BA.debugLineNum = 2005;BA.debugLine="mB(9)= 174";
_mb[(int) (9)] = (int) (174);
 //BA.debugLineNum = 2006;BA.debugLine="mB(10)= 178";
_mb[(int) (10)] = (int) (178);
 //BA.debugLineNum = 2007;BA.debugLine="mB(11)= 182";
_mb[(int) (11)] = (int) (182);
 //BA.debugLineNum = 2008;BA.debugLine="mB(12)= 185";
_mb[(int) (12)] = (int) (185);
 //BA.debugLineNum = 2009;BA.debugLine="mB(13)= 189";
_mb[(int) (13)] = (int) (189);
 //BA.debugLineNum = 2010;BA.debugLine="mB(14)= 191";
_mb[(int) (14)] = (int) (191);
 //BA.debugLineNum = 2011;BA.debugLine="mB(15)= 193";
_mb[(int) (15)] = (int) (193);
 //BA.debugLineNum = 2012;BA.debugLine="mB(16)= 194";
_mb[(int) (16)] = (int) (194);
 //BA.debugLineNum = 2013;BA.debugLine="mB(17)= 195";
_mb[(int) (17)] = (int) (195);
 //BA.debugLineNum = 2014;BA.debugLine="mB(18)= 194";
_mb[(int) (18)] = (int) (194);
 //BA.debugLineNum = 2015;BA.debugLine="mB(19)= 187";
_mb[(int) (19)] = (int) (187);
 //BA.debugLineNum = 2016;BA.debugLine="mB(20)= 186";
_mb[(int) (20)] = (int) (186);
 //BA.debugLineNum = 2017;BA.debugLine="mB(21)= 187";
_mb[(int) (21)] = (int) (187);
 //BA.debugLineNum = 2018;BA.debugLine="mB(22)= 186";
_mb[(int) (22)] = (int) (186);
 //BA.debugLineNum = 2020;BA.debugLine="mB(23)= 182";
_mb[(int) (23)] = (int) (182);
 //BA.debugLineNum = 2021;BA.debugLine="mB(24)= 182";
_mb[(int) (24)] = (int) (182);
 //BA.debugLineNum = 2022;BA.debugLine="mB(25)= 178";
_mb[(int) (25)] = (int) (178);
 //BA.debugLineNum = 2023;BA.debugLine="mB(26)= 178";
_mb[(int) (26)] = (int) (178);
 //BA.debugLineNum = 2024;BA.debugLine="mB(27)= 176";
_mb[(int) (27)] = (int) (176);
 //BA.debugLineNum = 2025;BA.debugLine="mB(28)= 175";
_mb[(int) (28)] = (int) (175);
 //BA.debugLineNum = 2026;BA.debugLine="mB(29)= 186";
_mb[(int) (29)] = (int) (186);
 //BA.debugLineNum = 2027;BA.debugLine="mB(30)= 194";
_mb[(int) (30)] = (int) (194);
 //BA.debugLineNum = 2028;BA.debugLine="mB(31)= 207";
_mb[(int) (31)] = (int) (207);
 //BA.debugLineNum = 2031;BA.debugLine="End Sub";
return "";
}
public static String  _cbsave_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 787;BA.debugLine="Sub CBsave_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 788;BA.debugLine="Starter.espera = 0";
mostCurrent._starter._espera /*int*/  = (int) (0);
 //BA.debugLineNum = 790;BA.debugLine="If (CBsave.Checked = True)  Then";
if ((mostCurrent._cbsave.getChecked()==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 791;BA.debugLine="If Starter.EstadoCBsabe = False Then";
if (mostCurrent._starter._estadocbsabe /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 792;BA.debugLine="Starter.bep.Initialize(100,600)";
mostCurrent._starter._bep /*anywheresoftware.b4a.audio.Beeper*/ .Initialize((int) (100),(int) (600));
 //BA.debugLineNum = 793;BA.debugLine="Label5.Text =\"Start Rec data \" & DateTime.Time(";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Start Rec data "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())));
 //BA.debugLineNum = 794;BA.debugLine="Starter.NameFile = DateTime.Date(DateTime.Now)";
mostCurrent._starter._namefile /*String*/  = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 795;BA.debugLine="Starter.EstadoCBsabe = True";
mostCurrent._starter._estadocbsabe /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 796;BA.debugLine="Starter.Tescaneo = 0";
mostCurrent._starter._tescaneo /*int*/  = (int) (0);
 //BA.debugLineNum = 797;BA.debugLine="Starter.nWr = 0";
mostCurrent._starter._nwr /*int*/  = (int) (0);
 }else {
 };
 }else {
 //BA.debugLineNum = 803;BA.debugLine="Label5.Text = \"Stop Rec data  \" &  DateTime.Time";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Stop Rec data  "+anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())));
 //BA.debugLineNum = 804;BA.debugLine="Starter.EstadoCBsabe = False";
mostCurrent._starter._estadocbsabe /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 808;BA.debugLine="End Sub";
return "";
}
public static String  _dataavailable(String _service,anywheresoftware.b4a.objects.collections.Map _characteristics) throws Exception{
 //BA.debugLineNum = 871;BA.debugLine="Sub DataAvailable (Service As String, Characterist";
 //BA.debugLineNum = 881;BA.debugLine="End Sub";
return "";
}
public static String  _draw_sector(float _altura,float _azimut,float _magnitud,float _cx,float _cy,anywheresoftware.b4a.objects.B4XCanvas _pcan,int _grados,int _altu) throws Exception{
anywheresoftware.b4a.objects.B4XCanvas.B4XPath _p1 = null;
float _ang1 = 0f;
float _ang2 = 0f;
float _inc_ang = 0f;
float _x1 = 0f;
float _y1 = 0f;
float _xt = 0f;
float _yt = 0f;
float _r1 = 0f;
float _r2 = 0f;
float _mm = 0f;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _ff = null;
int _mpal = 0;
float _ranb = 0f;
int _np = 0;
 //BA.debugLineNum = 1228;BA.debugLine="Private Sub draw_sector(altura As Float, azimut As";
 //BA.debugLineNum = 1230;BA.debugLine="Dim p1 As B4XPath";
_p1 = new anywheresoftware.b4a.objects.B4XCanvas.B4XPath();
 //BA.debugLineNum = 1231;BA.debugLine="Private ang1, ang2, inc_ang, x1, y1, xt, yt, r1,";
_ang1 = 0f;
_ang2 = 0f;
_inc_ang = 0f;
_x1 = 0f;
_y1 = 0f;
_xt = 0f;
_yt = 0f;
_r1 = 0f;
_r2 = 0f;
 //BA.debugLineNum = 1232;BA.debugLine="Private mm As Float = magnitud";
_mm = _magnitud;
 //BA.debugLineNum = 1233;BA.debugLine="Dim ff As B4XFont = xui.CreateDefaultFont(8)";
_ff = _xui.CreateDefaultFont((float) (8));
 //BA.debugLineNum = 1234;BA.debugLine="Dim mpal As Int";
_mpal = 0;
 //BA.debugLineNum = 1235;BA.debugLine="Dim ranb As Float";
_ranb = 0f;
 //BA.debugLineNum = 1237;BA.debugLine="ang1 = azimut -1";
_ang1 = (float) (_azimut-1);
 //BA.debugLineNum = 1238;BA.debugLine="alto = altu";
_alto = _altu;
 //BA.debugLineNum = 1241;BA.debugLine="If (PanelMain.Width < 650) Then 'para tablet de 1";
if ((mostCurrent._panelmain.getWidth()<650)) { 
 //BA.debugLineNum = 1242;BA.debugLine="alto = altu/2";
_alto = (int) (_altu/(double)2);
 //BA.debugLineNum = 1243;BA.debugLine="altura = altura/2";
_altura = (float) (_altura/(double)2);
 };
 //BA.debugLineNum = 1246;BA.debugLine="ancho = grados";
_ancho = _grados;
 //BA.debugLineNum = 1247;BA.debugLine="ang2 = (azimut + ancho ) + 1";
_ang2 = (float) ((_azimut+_ancho)+1);
 //BA.debugLineNum = 1248;BA.debugLine="inc_ang = (ang2 - ang1) / 10.0";
_inc_ang = (float) ((_ang2-_ang1)/(double)10.0);
 //BA.debugLineNum = 1251;BA.debugLine="If AS145 = False Then";
if (_as145==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1253;BA.debugLine="altura = altura * 5.0";
_altura = (float) (_altura*5.0);
 //BA.debugLineNum = 1254;BA.debugLine="r1 = altura - 1";
_r1 = (float) (_altura-1);
 //BA.debugLineNum = 1256;BA.debugLine="r2 = (altura + alto * 5) + 1";
_r2 = (float) ((_altura+_alto*5)+1);
 }else {
 //BA.debugLineNum = 1258;BA.debugLine="altura = altura * 6.0";
_altura = (float) (_altura*6.0);
 //BA.debugLineNum = 1259;BA.debugLine="r1 = altura - 1";
_r1 = (float) (_altura-1);
 //BA.debugLineNum = 1260;BA.debugLine="r2 = (altura + alto * 6) + 1";
_r2 = (float) ((_altura+_alto*6)+1);
 };
 //BA.debugLineNum = 1264;BA.debugLine="x1 = cx + r1 * CosD(ang1)";
_x1 = (float) (_cx+_r1*anywheresoftware.b4a.keywords.Common.CosD(_ang1));
 //BA.debugLineNum = 1265;BA.debugLine="y1 = cy + r1 * SinD(ang1)";
_y1 = (float) (_cy+_r1*anywheresoftware.b4a.keywords.Common.SinD(_ang1));
 //BA.debugLineNum = 1266;BA.debugLine="p1.Initialize(x1, y1)";
_p1.Initialize(_x1,_y1);
 //BA.debugLineNum = 1267;BA.debugLine="x1 = cx + r2 * CosD(ang1)";
_x1 = (float) (_cx+_r2*anywheresoftware.b4a.keywords.Common.CosD(_ang1));
 //BA.debugLineNum = 1268;BA.debugLine="y1 = cy + r2 * SinD(ang1)";
_y1 = (float) (_cy+_r2*anywheresoftware.b4a.keywords.Common.SinD(_ang1));
 //BA.debugLineNum = 1269;BA.debugLine="p1.LineTo(x1, y1)";
_p1.LineTo(_x1,_y1);
 //BA.debugLineNum = 1271;BA.debugLine="For np = 0 To 10";
{
final int step31 = 1;
final int limit31 = (int) (10);
_np = (int) (0) ;
for (;_np <= limit31 ;_np = _np + step31 ) {
 //BA.debugLineNum = 1272;BA.debugLine="x1 = cx + r2 * CosD(ang1 + inc_ang * np)";
_x1 = (float) (_cx+_r2*anywheresoftware.b4a.keywords.Common.CosD(_ang1+_inc_ang*_np));
 //BA.debugLineNum = 1273;BA.debugLine="y1 = cy + r2 * SinD(ang1 + inc_ang * np)";
_y1 = (float) (_cy+_r2*anywheresoftware.b4a.keywords.Common.SinD(_ang1+_inc_ang*_np));
 //BA.debugLineNum = 1274;BA.debugLine="p1.LineTo(x1, y1)";
_p1.LineTo(_x1,_y1);
 }
};
 //BA.debugLineNum = 1278;BA.debugLine="x1 = cx + r2 * CosD(ang2)";
_x1 = (float) (_cx+_r2*anywheresoftware.b4a.keywords.Common.CosD(_ang2));
 //BA.debugLineNum = 1279;BA.debugLine="y1 = cy + r2 * SinD(ang2)";
_y1 = (float) (_cy+_r2*anywheresoftware.b4a.keywords.Common.SinD(_ang2));
 //BA.debugLineNum = 1280;BA.debugLine="p1.LineTo(x1, y1)";
_p1.LineTo(_x1,_y1);
 //BA.debugLineNum = 1281;BA.debugLine="x1 = cx + r1 * CosD(ang2)";
_x1 = (float) (_cx+_r1*anywheresoftware.b4a.keywords.Common.CosD(_ang2));
 //BA.debugLineNum = 1282;BA.debugLine="y1 = cy + r1 * SinD(ang2)";
_y1 = (float) (_cy+_r1*anywheresoftware.b4a.keywords.Common.SinD(_ang2));
 //BA.debugLineNum = 1283;BA.debugLine="p1.LineTo(x1, y1)";
_p1.LineTo(_x1,_y1);
 //BA.debugLineNum = 1284;BA.debugLine="For np = 0 To 10";
{
final int step42 = 1;
final int limit42 = (int) (10);
_np = (int) (0) ;
for (;_np <= limit42 ;_np = _np + step42 ) {
 //BA.debugLineNum = 1285;BA.debugLine="x1 = cx + r1 * CosD(ang2 - inc_ang * np)";
_x1 = (float) (_cx+_r1*anywheresoftware.b4a.keywords.Common.CosD(_ang2-_inc_ang*_np));
 //BA.debugLineNum = 1286;BA.debugLine="y1 = cy + r1 * SinD(ang2 - inc_ang * np)";
_y1 = (float) (_cy+_r1*anywheresoftware.b4a.keywords.Common.SinD(_ang2-_inc_ang*_np));
 //BA.debugLineNum = 1287;BA.debugLine="p1.LineTo(x1, y1)";
_p1.LineTo(_x1,_y1);
 }
};
 //BA.debugLineNum = 1290;BA.debugLine="xt = cx + (r2+r1 + 25) / 2 * CosD((ang2+ang1)/2)";
_xt = (float) (_cx+(_r2+_r1+25)/(double)2*anywheresoftware.b4a.keywords.Common.CosD((_ang2+_ang1)/(double)2));
 //BA.debugLineNum = 1291;BA.debugLine="yt = cy + (r2+r1 + 25) / 2 * SinD((ang2+ang1)/2)";
_yt = (float) (_cy+(_r2+_r1+25)/(double)2*anywheresoftware.b4a.keywords.Common.SinD((_ang2+_ang1)/(double)2));
 //BA.debugLineNum = 1293;BA.debugLine="If magnitud = 0 Then";
if (_magnitud==0) { 
 //BA.debugLineNum = 1294;BA.debugLine="pCan.DrawPath(p1, xui.Color_ARGB(255, 255, 255,";
_pcan.DrawPath(_p1,_xui.Color_ARGB((int) (255),(int) (255),(int) (255),(int) (255)),anywheresoftware.b4a.keywords.Common.True,(float) (1));
 }else {
 //BA.debugLineNum = 1296;BA.debugLine="If CBcolor.Checked = False Then";
if (mostCurrent._cbcolor.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1297;BA.debugLine="colorgrafo = \"\"";
_colorgrafo = "";
 //BA.debugLineNum = 1298;BA.debugLine="magnitud = Brillomax - magnitud";
_magnitud = (float) (_brillomax-_magnitud);
 //BA.debugLineNum = 1299;BA.debugLine="Dim ranb As Float = rango * (Brillomax - Brillo";
_ranb = (float) (_rango*(_brillomax-_brillomin));
 //BA.debugLineNum = 1301;BA.debugLine="pCan.DrawPath(p1, xui.Color_ARGB(255, magnitud*";
_pcan.DrawPath(_p1,_xui.Color_ARGB((int) (255),(int) (_magnitud*255.0/(double)_ranb),(int) (_magnitud*255.0/(double)_ranb),(int) (_magnitud*255.0/(double)_ranb)),anywheresoftware.b4a.keywords.Common.True,(float) (1));
 }else {
 //BA.debugLineNum = 1303;BA.debugLine="colorgrafo = \"_c\"";
_colorgrafo = "_c";
 //BA.debugLineNum = 1304;BA.debugLine="ranb = 0.90 * (Brillomax - Brillomin)";
_ranb = (float) (0.90*(_brillomax-_brillomin));
 //BA.debugLineNum = 1305;BA.debugLine="CargaPaletaNixNox";
_cargapaletanixnox();
 //BA.debugLineNum = 1306;BA.debugLine="mpal = ((magnitud - Brillomin)*31)/ranb";
_mpal = (int) (((_magnitud-_brillomin)*31)/(double)_ranb);
 //BA.debugLineNum = 1307;BA.debugLine="If mpal > 31 Then";
if (_mpal>31) { 
 //BA.debugLineNum = 1308;BA.debugLine="mpal = 31";
_mpal = (int) (31);
 };
 //BA.debugLineNum = 1310;BA.debugLine="If mpal < 0  Then";
if (_mpal<0) { 
 //BA.debugLineNum = 1311;BA.debugLine="mpal = 0";
_mpal = (int) (0);
 };
 //BA.debugLineNum = 1313;BA.debugLine="If CBtir.Checked = False Then";
if (mostCurrent._cbtir.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1314;BA.debugLine="mpal = 31-mpal";
_mpal = (int) (31-_mpal);
 };
 //BA.debugLineNum = 1318;BA.debugLine="pCan.DrawPath(p1, xui.Color_ARGB(255, mR(mpal),";
_pcan.DrawPath(_p1,_xui.Color_ARGB((int) (255),_mr[_mpal],_mg[_mpal],_mb[_mpal]),anywheresoftware.b4a.keywords.Common.True,(float) (1));
 };
 };
 //BA.debugLineNum = 1329;BA.debugLine="If AS145 = False Then";
if (_as145==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1330;BA.debugLine="If (altura > 0)  Then";
if ((_altura>0)) { 
 //BA.debugLineNum = 1331;BA.debugLine="pCan.DrawText(NumberFormat( mm, 2, 2), xt, yt,";
_pcan.DrawText(processBA,anywheresoftware.b4a.keywords.Common.NumberFormat(_mm,(int) (2),(int) (2)),_xt,_yt,_ff,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 }else {
 //BA.debugLineNum = 1333;BA.debugLine="MagCenit = MagCenit + mm	'promedio de lecturas";
_magcenit = (float) (_magcenit+_mm);
 };
 }else {
 //BA.debugLineNum = 1336;BA.debugLine="If grados = 360 Then";
if (_grados==360) { 
 //BA.debugLineNum = 1337;BA.debugLine="pCan.DrawText(NumberFormat( mm, 2, 2), xt+10, y";
_pcan.DrawText(processBA,anywheresoftware.b4a.keywords.Common.NumberFormat(_mm,(int) (2),(int) (2)),(float) (_xt+10),_yt,_ff,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1340;BA.debugLine="If grados = 45 Then";
if (_grados==45) { 
 //BA.debugLineNum = 1341;BA.debugLine="pCan.DrawText(NumberFormat( mm, 2, 2), xt, yt,";
_pcan.DrawText(processBA,anywheresoftware.b4a.keywords.Common.NumberFormat(_mm,(int) (2),(int) (2)),_xt,_yt,_ff,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1345;BA.debugLine="If grados = 22 Then";
if (_grados==22) { 
 //BA.debugLineNum = 1346;BA.debugLine="pCan.DrawText(NumberFormat( mm, 2, 2), xt, yt,";
_pcan.DrawText(processBA,anywheresoftware.b4a.keywords.Common.NumberFormat(_mm,(int) (2),(int) (2)),_xt,_yt,_ff,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1349;BA.debugLine="If grados = 15 Then";
if (_grados==15) { 
 //BA.debugLineNum = 1350;BA.debugLine="pCan.DrawText(NumberFormat( mm, 2, 2), xt, yt,";
_pcan.DrawText(processBA,anywheresoftware.b4a.keywords.Common.NumberFormat(_mm,(int) (2),(int) (2)),_xt,_yt,_ff,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1354;BA.debugLine="If azimut = 0 Then";
if (_azimut==0) { 
 };
 };
 //BA.debugLineNum = 1360;BA.debugLine="End Sub";
return "";
}
public static String  _edittext1_enterpressed() throws Exception{
int _escrit = 0;
 //BA.debugLineNum = 1844;BA.debugLine="Sub EditText1_EnterPressed";
 //BA.debugLineNum = 1847;BA.debugLine="EditText1.TextColor = Colors.RGB(0x7F,0x9D,0xF9)";
mostCurrent._edittext1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 1849;BA.debugLine="Dim escrit As Int";
_escrit = 0;
 //BA.debugLineNum = 1851;BA.debugLine="TimBoton.Enabled = True";
_timboton.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1853;BA.debugLine="Starter.NameSensor = EditText1.Text";
mostCurrent._starter._namesensor /*String*/  = mostCurrent._edittext1.getText();
 //BA.debugLineNum = 1854;BA.debugLine="If (Starter.NameSensor.Length >3) Then";
if ((mostCurrent._starter._namesensor /*String*/ .length()>3)) { 
 //BA.debugLineNum = 1856;BA.debugLine="If File.Exists(rpcfg.GetSafeDirDefaultExternal(\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1857;BA.debugLine="ToastMessageShow(\"attachment file not found\", F";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("attachment file not found"),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 1859;BA.debugLine="If (File.Delete(rpcfg.GetSafeDirDefaultExternal";
if ((anywheresoftware.b4a.keywords.Common.File.Delete(_rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt"))) { 
 //BA.debugLineNum = 1860;BA.debugLine="Starter.espera = 0";
mostCurrent._starter._espera /*int*/  = (int) (0);
 //BA.debugLineNum = 1861;BA.debugLine="Label5.Text = \"cfg erased\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("cfg erased"));
 }else {
 //BA.debugLineNum = 1863;BA.debugLine="ToastMessageShow(\"No File found to delete\", Fa";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No File found to delete"),anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 1867;BA.debugLine="tsp_cfg.Initialize(rpcfg.GetSafeDirDefaultExtern";
_tsp_cfg.Initialize(_rpcfg.GetSafeDirDefaultExternal(""),"tsp_cfg.txt",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1868;BA.debugLine="escrit = tsp_cfg.WriteBytes(Starter.NameSensor.G";
_escrit = _tsp_cfg.WriteBytes(mostCurrent._starter._namesensor /*String*/ .getBytes("Windows-1252"),(int) (0),mostCurrent._starter._namesensor /*String*/ .length(),(long) (0));
 //BA.debugLineNum = 1870;BA.debugLine="Starter.espera = 0";
mostCurrent._starter._espera /*int*/  = (int) (0);
 //BA.debugLineNum = 1871;BA.debugLine="Label5.TextColor = Colors.LightGray";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 1872;BA.debugLine="Label5.Text = \"Selected  \" & Starter.NameSensor";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Selected  "+mostCurrent._starter._namesensor /*String*/ ));
 //BA.debugLineNum = 1873;BA.debugLine="tsp_cfg.Close";
_tsp_cfg.Close();
 //BA.debugLineNum = 1875;BA.debugLine="If Starter.NameSensor.Contains(\"TAS\") Then";
if (mostCurrent._starter._namesensor /*String*/ .contains("TAS")) { 
 //BA.debugLineNum = 1876;BA.debugLine="BstartTas.Enabled = True";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1878;BA.debugLine="BstartTas.Enabled = False";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1881;BA.debugLine="Starter.manager.Disconnect";
mostCurrent._starter._manager /*anywheresoftware.b4a.objects.BleManager2*/ .Disconnect();
 //BA.debugLineNum = 1882;BA.debugLine="Starter.RxByBT = 0";
mostCurrent._starter._rxbybt /*String*/  = BA.NumberToString(0);
 //BA.debugLineNum = 1883;BA.debugLine="Starter.esperapelin = 0";
mostCurrent._starter._esperapelin /*int*/  = (int) (0);
 };
 //BA.debugLineNum = 1886;BA.debugLine="End Sub";
return "";
}
public static String  _edittext1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 1839;BA.debugLine="Sub EditText1_TextChanged (Old As String, New As S";
 //BA.debugLineNum = 1840;BA.debugLine="EditText1.TextColor = Colors.Red";
mostCurrent._edittext1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 1841;BA.debugLine="End Sub";
return "";
}
public static String  _elugar_enterpressed() throws Exception{
 //BA.debugLineNum = 861;BA.debugLine="Sub Elugar_EnterPressed";
 //BA.debugLineNum = 862;BA.debugLine="Starter.Lugar = Elugar.Text";
mostCurrent._starter._lugar /*String*/  = mostCurrent._elugar.getText();
 //BA.debugLineNum = 863;BA.debugLine="TimBoton.Enabled = True";
_timboton.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 864;BA.debugLine="Elugar.TextColor = Colors.RGB(0x7F,0x9D,0xF9)";
mostCurrent._elugar.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0x7f),(int) (0x9d),(int) (0xf9)));
 //BA.debugLineNum = 865;BA.debugLine="End Sub";
return "";
}
public static String  _elugar_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 867;BA.debugLine="Sub Elugar_TextChanged (Old As String, New As Stri";
 //BA.debugLineNum = 868;BA.debugLine="Elugar.TextColor = Colors.Red";
mostCurrent._elugar.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 869;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 195;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 197;BA.debugLine="Dim Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 198;BA.debugLine="Dim Ltsensor As Label";
mostCurrent._ltsensor = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 199;BA.debugLine="Dim Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 201;BA.debugLine="Dim Label6 As Label";
mostCurrent._label6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 208;BA.debugLine="Dim Label11 As Label";
mostCurrent._label11 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 211;BA.debugLine="Dim CBsave As CheckBox";
mostCurrent._cbsave = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 213;BA.debugLine="Dim Label5 As Label";
mostCurrent._label5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 214;BA.debugLine="Dim EditText1 As EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 216;BA.debugLine="Dim Label9 As Label";
mostCurrent._label9 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 218;BA.debugLine="Private Bmqtt As Button";
mostCurrent._bmqtt = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 219;BA.debugLine="Private Lorientacion As Label";
mostCurrent._lorientacion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 220;BA.debugLine="Private Lcuenta As Label";
mostCurrent._lcuenta = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 225;BA.debugLine="Private TBtiempo As ToggleButton";
mostCurrent._tbtiempo = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 226;BA.debugLine="Private Elugar As EditText";
mostCurrent._elugar = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 228;BA.debugLine="Private Bgraph As Button";
mostCurrent._bgraph = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 229;BA.debugLine="Private PanelMain As Panel";
mostCurrent._panelmain = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 232;BA.debugLine="Private ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 233;BA.debugLine="Private Lversion As Label";
mostCurrent._lversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 234;BA.debugLine="Private Lnelm As Label";
mostCurrent._lnelm = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 235;BA.debugLine="Private Ltsensor As Label";
mostCurrent._ltsensor = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 236;BA.debugLine="Private LtempIR As Label";
mostCurrent._ltempir = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 238;BA.debugLine="Private BAlarmaIR As Button";
mostCurrent._balarmair = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 239;BA.debugLine="Private BAlarmaMV As Button";
mostCurrent._balarmamv = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 240;BA.debugLine="Private CBcolor As CheckBox";
mostCurrent._cbcolor = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 241;BA.debugLine="Private Lvbat As Label";
mostCurrent._lvbat = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 242;BA.debugLine="Private CBtir As CheckBox";
mostCurrent._cbtir = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 243;BA.debugLine="Private Bscan As Button";
mostCurrent._bscan = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 244;BA.debugLine="Private btnDisconnect As Button";
mostCurrent._btndisconnect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 245;BA.debugLine="Private btnReadData As Button";
mostCurrent._btnreaddata = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 246;BA.debugLine="Private BstartTas As Button";
mostCurrent._bstarttas = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 247;BA.debugLine="Private Linform As Label";
mostCurrent._linform = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 248;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 249;BA.debugLine="End Sub";
return "";
}
public static String  _listview1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 1770;BA.debugLine="Sub ListView1_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 1773;BA.debugLine="FichSel = ListView1.GetItem(Position)";
_fichsel = BA.ObjectToString(mostCurrent._listview1.GetItem(_position));
 //BA.debugLineNum = 1774;BA.debugLine="ListView1.Visible = False";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1775;BA.debugLine="PanelMain.Visible = True";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1776;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 1777;BA.debugLine="Label5.Text = \"Selec: \" & FichSel";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Selec: "+_fichsel));
 //BA.debugLineNum = 1779;BA.debugLine="If FichSel.IndexOf(\"_AS\") =  6 Then		'Auto Scan";
if (_fichsel.indexOf("_AS")==6) { 
 //BA.debugLineNum = 1780;BA.debugLine="AS145 = True";
_as145 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1783;BA.debugLine="If abrirfile145(FichSel) = False Then";
if (_abrirfile145(_fichsel)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1785;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1787;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1789;BA.debugLine="If numrowreal > 138 Then";
if (_numrowreal>138) { 
 //BA.debugLineNum = 1790;BA.debugLine="PreparaSectores145";
_preparasectores145();
 }else {
 //BA.debugLineNum = 1793;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1794;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 };
 }else if(_fichsel.indexOf("TAS")==0) { 
 //BA.debugLineNum = 1799;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1800;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 1801;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else if(_fichsel.indexOf("TSP")==0) { 
 //BA.debugLineNum = 1805;BA.debugLine="AS145 = False";
_as145 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1806;BA.debugLine="If abrirfile(FichSel) Then			  'Trata de represe";
if (_abrirfile(_fichsel)) { 
 //BA.debugLineNum = 1807;BA.debugLine="bz";
_bz();
 }else {
 //BA.debugLineNum = 1811;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1812;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 1813;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 }else {
 //BA.debugLineNum = 1819;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1820;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 1821;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 1828;BA.debugLine="End Sub";
return "";
}
public static float  _magnitudvisual(float _mag) throws Exception{
float _b = 0f;
float _v = 0f;
 //BA.debugLineNum = 1831;BA.debugLine="Sub MagnitudVisual( mag As Float) As Float";
 //BA.debugLineNum = 1833;BA.debugLine="Private b, v As Float";
_b = 0f;
_v = 0f;
 //BA.debugLineNum = 1834;BA.debugLine="b = Power(10, 4.316-(mag/5))";
_b = (float) (anywheresoftware.b4a.keywords.Common.Power(10,4.316-(_mag/(double)5)));
 //BA.debugLineNum = 1835;BA.debugLine="v = 7.93 - 5.0 * Logarithm(1 + b, 10)";
_v = (float) (7.93-5.0*anywheresoftware.b4a.keywords.Common.Logarithm(1+_b,10));
 //BA.debugLineNum = 1836;BA.debugLine="Return(v)";
if (true) return (_v);
 //BA.debugLineNum = 1837;BA.debugLine="End Sub";
return 0f;
}
public static String  _preparasectores() throws Exception{
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f10 = null;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f12 = null;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f14 = null;
String _fich = "";
anywheresoftware.b4a.objects.B4XCanvas.B4XRect _r = null;
int _naz = 0;
int _nal = 0;
 //BA.debugLineNum = 1539;BA.debugLine="Private Sub PreparaSectores";
 //BA.debugLineNum = 1541;BA.debugLine="PanelMain.RemoveAllViews ' para poder refrescar e";
mostCurrent._panelmain.RemoveAllViews();
 //BA.debugLineNum = 1543;BA.debugLine="Dim f10 As B4XFont = xui.CreateDefaultFont(8)";
_f10 = _xui.CreateDefaultFont((float) (8));
 //BA.debugLineNum = 1544;BA.debugLine="Dim f12 As B4XFont = xui.CreateDefaultFont(10)";
_f12 = _xui.CreateDefaultFont((float) (10));
 //BA.debugLineNum = 1545;BA.debugLine="Dim f14 As B4XFont = xui.CreateDefaultFont(12)";
_f14 = _xui.CreateDefaultFont((float) (12));
 //BA.debugLineNum = 1546;BA.debugLine="Private fich As String";
_fich = "";
 //BA.debugLineNum = 1547;BA.debugLine="Dim R As B4XRect";
_r = new anywheresoftware.b4a.objects.B4XCanvas.B4XRect();
 //BA.debugLineNum = 1548;BA.debugLine="Can.Initialize(PanelMain)";
_can.Initialize((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(mostCurrent._panelmain.getObject())));
 //BA.debugLineNum = 1550;BA.debugLine="R.Initialize(0,0,PanelMain.Width,PanelMain.Height";
_r.Initialize((float) (0),(float) (0),(float) (mostCurrent._panelmain.getWidth()),(float) (mostCurrent._panelmain.getHeight()));
 //BA.debugLineNum = 1551;BA.debugLine="Can.DrawRect(R,xui.Color_Black,True,1dip)";
_can.DrawRect(_r,_xui.Color_Black,anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 1553;BA.debugLine="MagCenit = 0";
_magcenit = (float) (0);
 //BA.debugLineNum = 1554;BA.debugLine="For naz = 0 To 11";
{
final int step11 = 1;
final int limit11 = (int) (11);
_naz = (int) (0) ;
for (;_naz <= limit11 ;_naz = _naz + step11 ) {
 //BA.debugLineNum = 1555;BA.debugLine="For nal = 0 To 3";
{
final int step12 = 1;
final int limit12 = (int) (3);
_nal = (int) (0) ;
for (;_nal <= limit12 ;_nal = _nal + step12 ) {
 //BA.debugLineNum = 1558;BA.debugLine="draw_sector((3-nal)*20, (naz)*30 - 105.0, mag2(";
_draw_sector((float) ((3-_nal)*20),(float) ((_naz)*30-105.0),_mag2[_nal][_naz],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (30),(int) (20));
 }
};
 }
};
 //BA.debugLineNum = 1564;BA.debugLine="Can.DrawText( Round2(MagCenit/12.0 ,2), PanelMain";
_can.DrawText(processBA,BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_magcenit/(double)12.0,(int) (2))),(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_f14,_xui.Color_Green,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1573;BA.debugLine="fich = fentrada.SubString2(0, fentrada.IndexOf(\".";
_fich = _fentrada.substring((int) (0),_fentrada.indexOf("."));
 //BA.debugLineNum = 1574;BA.debugLine="fich = fich & \"   \" & Hora";
_fich = _fich+"   "+_hora;
 //BA.debugLineNum = 1575;BA.debugLine="Can.DrawText(fich,PanelMain.Width/2, 20, f10, xui";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (20),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1576;BA.debugLine="Can.DrawText(Lati, 10, 50, f10, xui.Color_Gray, \"";
_can.DrawText(processBA,_lati,(float) (10),(float) (50),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1577;BA.debugLine="Can.DrawText(Longi, 10, 80, f10, xui.Color_Gray,";
_can.DrawText(processBA,_longi,(float) (10),(float) (80),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1579;BA.debugLine="Can.DrawText(HoraSl ,10, 110, f10, xui.Color_Gray";
_can.DrawText(processBA,_horasl,(float) (10),(float) (110),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1589;BA.debugLine="Can.DrawText(\"N\",PanelMain.Width -80,  120, f14,";
_can.DrawText(processBA,"N",(float) (mostCurrent._panelmain.getWidth()-80),(float) (120),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1590;BA.debugLine="Can.DrawText(\"S\",PanelMain.Width -80, 180, f14, x";
_can.DrawText(processBA,"S",(float) (mostCurrent._panelmain.getWidth()-80),(float) (180),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1591;BA.debugLine="Can.DrawText(\"W\",PanelMain.Width -110, 150, f14,";
_can.DrawText(processBA,"W",(float) (mostCurrent._panelmain.getWidth()-110),(float) (150),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1592;BA.debugLine="Can.DrawText(\"E\",PanelMain.Width -50, 150, f14, x";
_can.DrawText(processBA,"E",(float) (mostCurrent._panelmain.getWidth()-50),(float) (150),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1600;BA.debugLine="fich = \"Range: \" & Round2(Brillomin, 1) & \"  \" &";
_fich = "Range: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_brillomin,(int) (1)))+"  "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_brillomax,(int) (1)));
 //BA.debugLineNum = 1601;BA.debugLine="If CBcolor.Checked = False Then";
if (mostCurrent._cbcolor.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1602;BA.debugLine="Can.DrawText(fich, PanelMain.Width -114, PanelMa";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()-114),(float) (mostCurrent._panelmain.getHeight()-70),_f12,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 }else {
 //BA.debugLineNum = 1604;BA.debugLine="Can.DrawText(fich, PanelMain.Width -114, PanelMa";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()-114),(float) (mostCurrent._panelmain.getHeight()-70),_f12,_xui.Color_ARGB((int) (250),_mr[(int) (20)],_mg[(int) (20)],_mb[(int) (20)]),BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1606;BA.debugLine="Can.DrawText(\"TESS P\",PanelMain.Width -70, PanelM";
_can.DrawText(processBA,"TESS P",(float) (mostCurrent._panelmain.getWidth()-70),(float) (mostCurrent._panelmain.getHeight()-110),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1608;BA.debugLine="fich = \"T IR: \" & Round2(TempIRmin,1) & \" ºC\"";
_fich = "T IR: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tempirmin,(int) (1)))+" ºC";
 //BA.debugLineNum = 1609;BA.debugLine="Can.DrawText(fich, 20, PanelMain.Height-95, f10,";
_can.DrawText(processBA,_fich,(float) (20),(float) (mostCurrent._panelmain.getHeight()-95),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1610;BA.debugLine="fich = \"T sen: \" & Round2( TempAmb,1) & \" ºC\"";
_fich = "T sen: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tempamb,(int) (1)))+" ºC";
 //BA.debugLineNum = 1611;BA.debugLine="Can.DrawText(fich, 20, PanelMain.Height-65, f10,";
_can.DrawText(processBA,_fich,(float) (20),(float) (mostCurrent._panelmain.getHeight()-65),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1614;BA.debugLine="grafica = Can";
_grafica = _can;
 //BA.debugLineNum = 1615;BA.debugLine="Can.Invalidate";
_can.Invalidate();
 //BA.debugLineNum = 1616;BA.debugLine="Can.Release";
_can.Release();
 //BA.debugLineNum = 1618;BA.debugLine="Savebmpcsv";
_savebmpcsv();
 //BA.debugLineNum = 1620;BA.debugLine="End Sub";
return "";
}
public static String  _preparasectores145() throws Exception{
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f10 = null;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f12 = null;
anywheresoftware.b4a.objects.B4XViewWrapper.B4XFont _f14 = null;
String _fich = "";
anywheresoftware.b4a.objects.B4XCanvas.B4XRect _r = null;
int _fila = 0;
float _rota = 0f;
int _naz = 0;
 //BA.debugLineNum = 1623;BA.debugLine="Private Sub PreparaSectores145";
 //BA.debugLineNum = 1626;BA.debugLine="PanelMain.RemoveAllViews ' para poder refrescar e";
mostCurrent._panelmain.RemoveAllViews();
 //BA.debugLineNum = 1628;BA.debugLine="Dim f10 As B4XFont = xui.CreateDefaultFont(8)";
_f10 = _xui.CreateDefaultFont((float) (8));
 //BA.debugLineNum = 1629;BA.debugLine="Dim f12 As B4XFont = xui.CreateDefaultFont(10)";
_f12 = _xui.CreateDefaultFont((float) (10));
 //BA.debugLineNum = 1630;BA.debugLine="Dim f14 As B4XFont = xui.CreateDefaultFont(12)";
_f14 = _xui.CreateDefaultFont((float) (12));
 //BA.debugLineNum = 1631;BA.debugLine="Private fich As String";
_fich = "";
 //BA.debugLineNum = 1632;BA.debugLine="Dim R As B4XRect";
_r = new anywheresoftware.b4a.objects.B4XCanvas.B4XRect();
 //BA.debugLineNum = 1633;BA.debugLine="Dim fila As Int";
_fila = 0;
 //BA.debugLineNum = 1634;BA.debugLine="Dim rota As Float";
_rota = 0f;
 //BA.debugLineNum = 1636;BA.debugLine="rota = 90";
_rota = (float) (90);
 //BA.debugLineNum = 1638;BA.debugLine="Can.Initialize(PanelMain)";
_can.Initialize((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(mostCurrent._panelmain.getObject())));
 //BA.debugLineNum = 1639;BA.debugLine="R.Initialize(0,0,PanelMain.Width,PanelMain.Height";
_r.Initialize((float) (0),(float) (0),(float) (mostCurrent._panelmain.getWidth()),(float) (mostCurrent._panelmain.getHeight()));
 //BA.debugLineNum = 1640;BA.debugLine="Can.DrawRect(R,xui.Color_Black,True,1dip)";
_can.DrawRect(_r,_xui.Color_Black,anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 1644;BA.debugLine="Brillomax = MVmax";
_brillomax = _mvmax;
 //BA.debugLineNum = 1645;BA.debugLine="Brillomin = MVmin";
_brillomin = _mvmin;
 //BA.debugLineNum = 1647;BA.debugLine="For naz = 1 To 32";
{
final int step15 = 1;
final int limit15 = (int) (32);
_naz = (int) (1) ;
for (;_naz <= limit15 ;_naz = _naz + step15 ) {
 //BA.debugLineNum = 1648;BA.debugLine="fila = naz";
_fila = _naz;
 //BA.debugLineNum = 1650;BA.debugLine="draw_sector(64,  naz*11.25  -(rota+11.25+11.25/2";
_draw_sector((float) (64),(float) (_naz*11.25-(_rota+11.25+11.25/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (11),(int) (8));
 }
};
 //BA.debugLineNum = 1652;BA.debugLine="For naz = 1 To 28";
{
final int step19 = 1;
final int limit19 = (int) (28);
_naz = (int) (1) ;
for (;_naz <= limit19 ;_naz = _naz + step19 ) {
 //BA.debugLineNum = 1653;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1654;BA.debugLine="draw_sector(56,  naz*12.86  -(rota+12.86+12.86/2";
_draw_sector((float) (56),(float) (_naz*12.86-(_rota+12.86+12.86/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (12),(int) (8));
 }
};
 //BA.debugLineNum = 1656;BA.debugLine="For naz = 1 To 24";
{
final int step23 = 1;
final int limit23 = (int) (24);
_naz = (int) (1) ;
for (;_naz <= limit23 ;_naz = _naz + step23 ) {
 //BA.debugLineNum = 1657;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1658;BA.debugLine="draw_sector(48,  naz*15  -(rota+15+15/2),   MagA";
_draw_sector((float) (48),(float) (_naz*15-(_rota+15+15/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (15),(int) (8));
 }
};
 //BA.debugLineNum = 1660;BA.debugLine="For naz = 1 To 20";
{
final int step27 = 1;
final int limit27 = (int) (20);
_naz = (int) (1) ;
for (;_naz <= limit27 ;_naz = _naz + step27 ) {
 //BA.debugLineNum = 1661;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1662;BA.debugLine="draw_sector(40,  naz*18  -(rota+18+18/2),   MagA";
_draw_sector((float) (40),(float) (_naz*18-(_rota+18+18/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (18),(int) (8));
 }
};
 //BA.debugLineNum = 1664;BA.debugLine="For naz = 1 To 16";
{
final int step31 = 1;
final int limit31 = (int) (16);
_naz = (int) (1) ;
for (;_naz <= limit31 ;_naz = _naz + step31 ) {
 //BA.debugLineNum = 1665;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1666;BA.debugLine="draw_sector(32,  naz*22.5  -(rota+22.5+22.5/2),";
_draw_sector((float) (32),(float) (_naz*22.5-(_rota+22.5+22.5/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (22),(int) (8));
 }
};
 //BA.debugLineNum = 1668;BA.debugLine="For naz = 1 To 12";
{
final int step35 = 1;
final int limit35 = (int) (12);
_naz = (int) (1) ;
for (;_naz <= limit35 ;_naz = _naz + step35 ) {
 //BA.debugLineNum = 1669;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1670;BA.debugLine="draw_sector(24,  naz*30  -(rota+30+30/2),   MagA";
_draw_sector((float) (24),(float) (_naz*30-(_rota+30+30/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (30),(int) (8));
 }
};
 //BA.debugLineNum = 1672;BA.debugLine="For naz = 1 To 8";
{
final int step39 = 1;
final int limit39 = (int) (8);
_naz = (int) (1) ;
for (;_naz <= limit39 ;_naz = _naz + step39 ) {
 //BA.debugLineNum = 1673;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1674;BA.debugLine="draw_sector(16,  naz*45 -(rota+45+45/2) ,   MagA";
_draw_sector((float) (16),(float) (_naz*45-(_rota+45+45/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (45),(int) (8));
 }
};
 //BA.debugLineNum = 1676;BA.debugLine="For naz = 1 To 4";
{
final int step43 = 1;
final int limit43 = (int) (4);
_naz = (int) (1) ;
for (;_naz <= limit43 ;_naz = _naz + step43 ) {
 //BA.debugLineNum = 1677;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1678;BA.debugLine="draw_sector(8,  naz*90  -(rota+90+90/2),   MagAS";
_draw_sector((float) (8),(float) (_naz*90-(_rota+90+90/(double)2)),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (90),(int) (8));
 }
};
 //BA.debugLineNum = 1681;BA.debugLine="fila = fila +1";
_fila = (int) (_fila+1);
 //BA.debugLineNum = 1682;BA.debugLine="draw_sector(0,  0 ,   MagAS(fila), PanelMain.Wid";
_draw_sector((float) (0),(float) (0),_magas[_fila],(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (mostCurrent._panelmain.getHeight()/(double)2),_can,(int) (360),(int) (8));
 //BA.debugLineNum = 1689;BA.debugLine="fich = fentrada.SubString2(0, fentrada.IndexOf(\".";
_fich = _fentrada.substring((int) (0),_fentrada.indexOf("."));
 //BA.debugLineNum = 1691;BA.debugLine="Can.DrawText(fich,PanelMain.Width/2, 20, f10, xui";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()/(double)2),(float) (20),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1692;BA.debugLine="Can.DrawText(Lati, 10, 50, f10, xui.Color_Gray, \"";
_can.DrawText(processBA,_lati,(float) (10),(float) (50),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1693;BA.debugLine="Can.DrawText(Longi, 10, 80, f10, xui.Color_Gray,";
_can.DrawText(processBA,_longi,(float) (10),(float) (80),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1695;BA.debugLine="Can.DrawText(HoraSl ,10, 110, f10, xui.Color_Gray";
_can.DrawText(processBA,_horasl,(float) (10),(float) (110),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1697;BA.debugLine="Can.DrawText(\"N\",PanelMain.Width -60, 80, f14, xu";
_can.DrawText(processBA,"N",(float) (mostCurrent._panelmain.getWidth()-60),(float) (80),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1698;BA.debugLine="Can.DrawText(\"S\",PanelMain.Width -60, 140, f14, x";
_can.DrawText(processBA,"S",(float) (mostCurrent._panelmain.getWidth()-60),(float) (140),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1699;BA.debugLine="Can.DrawText(\"W\",PanelMain.Width -90, 110, f14, x";
_can.DrawText(processBA,"W",(float) (mostCurrent._panelmain.getWidth()-90),(float) (110),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1700;BA.debugLine="Can.DrawText(\"E\",PanelMain.Width -30, 110, f14, x";
_can.DrawText(processBA,"E",(float) (mostCurrent._panelmain.getWidth()-30),(float) (110),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1708;BA.debugLine="fich = \"Range: \" & Round2(Brillomin, 1) & \"  \" &";
_fich = "Range: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_brillomin,(int) (1)))+"  "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_brillomax,(int) (1)));
 //BA.debugLineNum = 1709;BA.debugLine="If CBcolor.Checked = False Then";
if (mostCurrent._cbcolor.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1710;BA.debugLine="Can.DrawText(fich, PanelMain.Width -114, PanelMa";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()-114),(float) (mostCurrent._panelmain.getHeight()-80),_f12,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 }else {
 //BA.debugLineNum = 1712;BA.debugLine="Can.DrawText(fich, PanelMain.Width -114, PanelMa";
_can.DrawText(processBA,_fich,(float) (mostCurrent._panelmain.getWidth()-114),(float) (mostCurrent._panelmain.getHeight()-80),_f12,_xui.Color_ARGB((int) (250),_mr[(int) (20)],_mg[(int) (20)],_mb[(int) (20)]),BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 };
 //BA.debugLineNum = 1714;BA.debugLine="Can.DrawText(\"TESS P\",PanelMain.Width -70, PanelM";
_can.DrawText(processBA,"TESS P",(float) (mostCurrent._panelmain.getWidth()-70),(float) (mostCurrent._panelmain.getHeight()-110),_f14,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 1716;BA.debugLine="fich = \"T IR: \" & Round2(TempIRmin,1) & \" ºC\"";
_fich = "T IR: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tempirmin,(int) (1)))+" ºC";
 //BA.debugLineNum = 1717;BA.debugLine="Can.DrawText(fich, 10, PanelMain.Height-110, f10,";
_can.DrawText(processBA,_fich,(float) (10),(float) (mostCurrent._panelmain.getHeight()-110),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1718;BA.debugLine="fich = \"T sen: \" & Round2( TempAmb,1) & \" ºC\"";
_fich = "T sen: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_tempamb,(int) (1)))+" ºC";
 //BA.debugLineNum = 1719;BA.debugLine="Can.DrawText(fich, 10, PanelMain.Height-80, f10,";
_can.DrawText(processBA,_fich,(float) (10),(float) (mostCurrent._panelmain.getHeight()-80),_f10,_xui.Color_Gray,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 1722;BA.debugLine="grafica = Can";
_grafica = _can;
 //BA.debugLineNum = 1723;BA.debugLine="Can.Invalidate";
_can.Invalidate();
 //BA.debugLineNum = 1724;BA.debugLine="Can.Release";
_can.Release();
 //BA.debugLineNum = 1725;BA.debugLine="Savebmpcsv";
_savebmpcsv();
 //BA.debugLineNum = 1726;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 131;BA.debugLine="Dim Tim1s As Timer";
_tim1s = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 132;BA.debugLine="Dim TimBoton As Timer";
_timboton = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 142;BA.debugLine="Dim mqtt_server_local As String = \"test.mosquitto";
_mqtt_server_local = "test.mosquitto.org";
 //BA.debugLineNum = 143;BA.debugLine="Dim mqtt_user_local As String = \"\"";
_mqtt_user_local = "";
 //BA.debugLineNum = 144;BA.debugLine="Dim mqtt_password_local As String = \"\"";
_mqtt_password_local = "";
 //BA.debugLineNum = 151;BA.debugLine="Dim my_topic As String = \"STARS4ALL/pruebaj/readi";
_my_topic = "STARS4ALL/pruebaj/reading";
 //BA.debugLineNum = 152;BA.debugLine="Dim myconmq As MqttConnectOptions";
_myconmq = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 153;BA.debugLine="Private mqtt As MqttClient";
_mqtt = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper();
 //BA.debugLineNum = 154;BA.debugLine="Dim mqconectado As Boolean";
_mqconectado = false;
 //BA.debugLineNum = 156;BA.debugLine="Private  difacimut As Int";
_difacimut = 0;
 //BA.debugLineNum = 157;BA.debugLine="Private LatLongSl As String";
_latlongsl = "";
 //BA.debugLineNum = 158;BA.debugLine="Private Lati, Longi, HoraSl, Hora As String";
_lati = "";
_longi = "";
_horasl = "";
_hora = "";
 //BA.debugLineNum = 159;BA.debugLine="Dim xui As XUI 'ignore";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 160;BA.debugLine="Private Brillomax, Brillomin As Float";
_brillomax = 0f;
_brillomin = 0f;
 //BA.debugLineNum = 161;BA.debugLine="Private MVmin, MVmax As Float";
_mvmin = 0f;
_mvmax = 0f;
 //BA.debugLineNum = 162;BA.debugLine="Private TempIRmin, TempAmb As Float";
_tempirmin = 0f;
_tempamb = 0f;
 //BA.debugLineNum = 163;BA.debugLine="Dim rango As Float = 1.9";
_rango = (float) (1.9);
 //BA.debugLineNum = 164;BA.debugLine="Private mag2(4, 12) As Float";
_mag2 = new float[(int) (4)][];
{
int d0 = _mag2.length;
int d1 = (int) (12);
for (int i0 = 0;i0 < d0;i0++) {
_mag2[i0] = new float[d1];
}
}
;
 //BA.debugLineNum = 165;BA.debugLine="Private ancho As Int = 30";
_ancho = (int) (30);
 //BA.debugLineNum = 166;BA.debugLine="Private alto As Int = 20";
_alto = (int) (20);
 //BA.debugLineNum = 167;BA.debugLine="Dim Can As B4XCanvas";
_can = new anywheresoftware.b4a.objects.B4XCanvas();
 //BA.debugLineNum = 168;BA.debugLine="Private fentrada As String";
_fentrada = "";
 //BA.debugLineNum = 169;BA.debugLine="Private grafica As B4XCanvas";
_grafica = new anywheresoftware.b4a.objects.B4XCanvas();
 //BA.debugLineNum = 170;BA.debugLine="Private MagCenit As Float";
_magcenit = 0f;
 //BA.debugLineNum = 171;BA.debugLine="Private FichSel As String";
_fichsel = "";
 //BA.debugLineNum = 173;BA.debugLine="Dim rpcfg As RuntimePermissions";
_rpcfg = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 174;BA.debugLine="Dim tsp_cfg As RandomAccessFile";
_tsp_cfg = new anywheresoftware.b4a.randomaccessfile.RandomAccessFile();
 //BA.debugLineNum = 175;BA.debugLine="Dim scaner As Boolean = False";
_scaner = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 176;BA.debugLine="Dim altertxt As Int";
_altertxt = 0;
 //BA.debugLineNum = 177;BA.debugLine="Private MagAS(146) As Float";
_magas = new float[(int) (146)];
;
 //BA.debugLineNum = 178;BA.debugLine="Private AS145 As Boolean";
_as145 = false;
 //BA.debugLineNum = 179;BA.debugLine="Private mR(32) As Int";
_mr = new int[(int) (32)];
;
 //BA.debugLineNum = 180;BA.debugLine="Private mG(32) As Int";
_mg = new int[(int) (32)];
;
 //BA.debugLineNum = 181;BA.debugLine="Private mB(32) As Int";
_mb = new int[(int) (32)];
;
 //BA.debugLineNum = 182;BA.debugLine="Private colorgrafo As String";
_colorgrafo = "";
 //BA.debugLineNum = 183;BA.debugLine="Dim udpsocket1 As UDPSocket";
_udpsocket1 = new anywheresoftware.b4a.objects.SocketWrapper.UDPSocket();
 //BA.debugLineNum = 187;BA.debugLine="Private numrowreal As Int";
_numrowreal = 0;
 //BA.debugLineNum = 189;BA.debugLine="Public AvisoSensorFound As Boolean";
_avisosensorfound = false;
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _savebmpcsv() throws Exception{
anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper _mybm = null;
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
String _myfn = "";
 //BA.debugLineNum = 1730;BA.debugLine="Sub Savebmpcsv    'guarda un bmp y un csv";
 //BA.debugLineNum = 1731;BA.debugLine="Private mybm As B4XBitmap";
_mybm = new anywheresoftware.b4a.objects.B4XViewWrapper.B4XBitmapWrapper();
 //BA.debugLineNum = 1732;BA.debugLine="Dim Out As OutputStream";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 1733;BA.debugLine="Private myfn As String";
_myfn = "";
 //BA.debugLineNum = 1734;BA.debugLine="If fentrada.Length > 0 Then";
if (_fentrada.length()>0) { 
 //BA.debugLineNum = 1735;BA.debugLine="myfn = fentrada.SubString2(0, fentrada.IndexOf(\"";
_myfn = _fentrada.substring((int) (0),_fentrada.indexOf("."));
 //BA.debugLineNum = 1737;BA.debugLine="If AS145 = False Then";
if (_as145==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1738;BA.debugLine="File.Copy(rpcfg.GetSafeDirDefaultExternal(\"\"),";
anywheresoftware.b4a.keywords.Common.File.Copy(_rpcfg.GetSafeDirDefaultExternal(""),"sel.txt",_rpcfg.GetSafeDirDefaultExternal(""),_myfn+"_out.csv");
 };
 //BA.debugLineNum = 1741;BA.debugLine="mybm = grafica.CreateBitmap";
_mybm = _grafica.CreateBitmap();
 //BA.debugLineNum = 1743;BA.debugLine="If CBtir.Checked = False Then";
if (mostCurrent._cbtir.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1744;BA.debugLine="Out = File.OpenOutput(rpcfg.GetSafeDirDefaultEx";
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(_rpcfg.GetSafeDirDefaultExternal(""),_myfn+_colorgrafo+".png",anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 1746;BA.debugLine="Out = File.OpenOutput(rpcfg.GetSafeDirDefaultEx";
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(_rpcfg.GetSafeDirDefaultExternal(""),_myfn+_colorgrafo+"_IR.png",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1752;BA.debugLine="mybm.WriteToStream(Out, 100, \"PNG\")";
_mybm.WriteToStream((java.io.OutputStream)(_out.getObject()),(int) (100),BA.getEnumFromString(android.graphics.Bitmap.CompressFormat.class,"PNG"));
 //BA.debugLineNum = 1753;BA.debugLine="Out.Close";
_out.Close();
 //BA.debugLineNum = 1756;BA.debugLine="File.Delete(rpcfg.GetSafeDirDefaultExternal(\"\"),";
anywheresoftware.b4a.keywords.Common.File.Delete(_rpcfg.GetSafeDirDefaultExternal(""),"sel.txt");
 }else {
 };
 //BA.debugLineNum = 1761;BA.debugLine="End Sub";
return "";
}
public static String  _statechanged() throws Exception{
 //BA.debugLineNum = 451;BA.debugLine="Public Sub StateChanged";
 //BA.debugLineNum = 453;BA.debugLine="Lcuenta.Text = Starter.currentStateText";
mostCurrent._lcuenta.setText(BA.ObjectToCharSequence(mostCurrent._starter._currentstatetext /*String*/ ));
 //BA.debugLineNum = 454;BA.debugLine="If Starter.connected Then";
if (mostCurrent._starter._connected /*boolean*/ ) { 
 //BA.debugLineNum = 455;BA.debugLine="Label5.Text = \"Connected: \" & Starter.ConnectedN";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Connected: "+mostCurrent._starter._connectedname /*String*/ +"   dB "+BA.NumberToString(mostCurrent._starter._btrssi /*int*/ )));
 }else {
 //BA.debugLineNum = 458;BA.debugLine="Label5.Text = \"Not connected\"";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Not connected"));
 };
 //BA.debugLineNum = 460;BA.debugLine="btnDisconnect.Enabled = Starter.connected";
mostCurrent._btndisconnect.setEnabled(mostCurrent._starter._connected /*boolean*/ );
 //BA.debugLineNum = 461;BA.debugLine="Bscan.Enabled = Not(Starter.connected)";
mostCurrent._bscan.setEnabled(anywheresoftware.b4a.keywords.Common.Not(mostCurrent._starter._connected /*boolean*/ ));
 //BA.debugLineNum = 464;BA.debugLine="btnReadData.Enabled = Starter.connected";
mostCurrent._btnreaddata.setEnabled(mostCurrent._starter._connected /*boolean*/ );
 //BA.debugLineNum = 465;BA.debugLine="Bscan.Enabled = (Starter.currentState = Starter.m";
mostCurrent._bscan.setEnabled((mostCurrent._starter._currentstate /*int*/ ==mostCurrent._starter._manager /*anywheresoftware.b4a.objects.BleManager2*/ .STATE_POWERED_ON) && mostCurrent._starter._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 469;BA.debugLine="End Sub";
return "";
}
public static String  _tbtiempo_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 836;BA.debugLine="Sub TBtiempo_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 846;BA.debugLine="If Starter.TiempoSalvarSg  = 1 Then";
if (mostCurrent._starter._tiemposalvarsg /*int*/ ==1) { 
 //BA.debugLineNum = 847;BA.debugLine="Starter.TiempoSalvarSg  =  10";
mostCurrent._starter._tiemposalvarsg /*int*/  = (int) (10);
 }else if(mostCurrent._starter._tiemposalvarsg /*int*/ <60) { 
 //BA.debugLineNum = 849;BA.debugLine="Starter.TiempoSalvarSg  = Starter.TiempoSalvarSg";
mostCurrent._starter._tiemposalvarsg /*int*/  = (int) (mostCurrent._starter._tiemposalvarsg /*int*/ +10);
 }else {
 //BA.debugLineNum = 851;BA.debugLine="Starter.TiempoSalvarSg  = 1";
mostCurrent._starter._tiemposalvarsg /*int*/  = (int) (1);
 };
 //BA.debugLineNum = 855;BA.debugLine="TBtiempo.TextOn = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOn(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 856;BA.debugLine="TBtiempo.TextOff = Starter.TiempoSalvarSg & \" s\"";
mostCurrent._tbtiempo.setTextOff(BA.ObjectToCharSequence(BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 857;BA.debugLine="Label5.Text = \" Record time \" & Starter.TiempoSal";
mostCurrent._label5.setText(BA.ObjectToCharSequence(" Record time "+BA.NumberToString(mostCurrent._starter._tiemposalvarsg /*int*/ )+" s"));
 //BA.debugLineNum = 858;BA.debugLine="End Sub";
return "";
}
public static String  _tim1s_tick() throws Exception{
float _batery = 0f;
 //BA.debugLineNum = 473;BA.debugLine="Sub Tim1s_Tick  ' Timer de 1 sg";
 //BA.debugLineNum = 475;BA.debugLine="Dim batery As Float";
_batery = 0f;
 //BA.debugLineNum = 479;BA.debugLine="If scaner = False Then		'Medidas suletas  ----";
if (_scaner==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 480;BA.debugLine="CBsave.Text = \"Save\"";
mostCurrent._cbsave.setText(BA.ObjectToCharSequence("Save"));
 //BA.debugLineNum = 481;BA.debugLine="CBsave.Enabled = True";
mostCurrent._cbsave.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 482;BA.debugLine="TBtiempo.Visible = True";
mostCurrent._tbtiempo.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 483;BA.debugLine="Elugar.Enabled = True";
mostCurrent._elugar.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 485;BA.debugLine="CBsave.Checked = False";
mostCurrent._cbsave.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 486;BA.debugLine="CBsave.Enabled = False";
mostCurrent._cbsave.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 487;BA.debugLine="CBsave.Text = \"Auto\"";
mostCurrent._cbsave.setText(BA.ObjectToCharSequence("Auto"));
 //BA.debugLineNum = 488;BA.debugLine="TBtiempo.Visible = False";
mostCurrent._tbtiempo.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 489;BA.debugLine="Elugar.Enabled = False";
mostCurrent._elugar.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 491;BA.debugLine="Linform.Text = \"Rec: \" & Starter.nWr";
mostCurrent._linform.setText(BA.ObjectToCharSequence("Rec: "+BA.NumberToString(mostCurrent._starter._nwr /*int*/ )));
 };
 //BA.debugLineNum = 494;BA.debugLine="Label1.Text =\"Lat \"&  Starter.Lat &  \"  Lon \" & S";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Lat "+mostCurrent._starter._lat /*String*/ +"  Lon "+mostCurrent._starter._lon /*String*/ +"  Alt "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._alt /*float*/ ,(int) (0)))));
 //BA.debugLineNum = 496;BA.debugLine="If Starter.CuentaSend > 0 Then";
if (mostCurrent._starter._cuentasend /*int*/ >0) { 
 //BA.debugLineNum = 497;BA.debugLine="Lcuenta.Text  = \"Rec: \" & Starter.nWr & \"   Send";
mostCurrent._lcuenta.setText(BA.ObjectToCharSequence("Rec: "+BA.NumberToString(mostCurrent._starter._nwr /*int*/ )+"   Send: "+BA.NumberToString(mostCurrent._starter._cuentasend /*int*/ )));
 }else {
 //BA.debugLineNum = 499;BA.debugLine="Lcuenta.Text  = \"Rec: \" & Starter.nWr";
mostCurrent._lcuenta.setText(BA.ObjectToCharSequence("Rec: "+BA.NumberToString(mostCurrent._starter._nwr /*int*/ )));
 };
 //BA.debugLineNum = 502;BA.debugLine="If Starter.nWr = 145 Then";
if (mostCurrent._starter._nwr /*int*/ ==145) { 
 //BA.debugLineNum = 503;BA.debugLine="Starter.nWr = 0";
mostCurrent._starter._nwr /*int*/  = (int) (0);
 //BA.debugLineNum = 504;BA.debugLine="Lcuenta.TextColor = Colors.Green";
mostCurrent._lcuenta.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 505;BA.debugLine="Lcuenta.Text  = \"Scan of \" & Starter.nWr & \" poi";
mostCurrent._lcuenta.setText(BA.ObjectToCharSequence("Scan of "+BA.NumberToString(mostCurrent._starter._nwr /*int*/ )+" points OK"));
 //BA.debugLineNum = 506;BA.debugLine="Linform.Visible = False";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 507;BA.debugLine="Log(\"----quito inform 145\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6458786","----quito inform 145",0);
 //BA.debugLineNum = 509;BA.debugLine="If	abrirfile145(Starter.nombreFich) = False Then";
if (_abrirfile145(mostCurrent._starter._nombrefich /*String*/ )==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 510;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 511;BA.debugLine="ListView1.Visible = True";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 513;BA.debugLine="AS145 = True";
_as145 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 514;BA.debugLine="ListView1.Visible = False";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 515;BA.debugLine="PanelMain.Visible = True";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 516;BA.debugLine="PreparaSectores145";
_preparasectores145();
 };
 }else {
 //BA.debugLineNum = 519;BA.debugLine="Lcuenta.TextColor = Colors.LightGray";
mostCurrent._lcuenta.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 };
 //BA.debugLineNum = 523;BA.debugLine="If (Starter.EsperaMsg = 0) And AvisoSensorFound =";
if ((mostCurrent._starter._esperamsg /*int*/ ==0) && _avisosensorfound==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 524;BA.debugLine="Label5.Text = DateTime.Time(DateTime.Now) & \" Se";
mostCurrent._label5.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" Sensor OK.  "+mostCurrent._starter._inform /*String*/ ));
 //BA.debugLineNum = 525;BA.debugLine="Label5.TextColor = Colors.LightGray";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 526;BA.debugLine="AvisoSensorFound = False";
_avisosensorfound = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 530;BA.debugLine="If (Starter.esperapelin < 8)   Then";
if ((mostCurrent._starter._esperapelin /*int*/ <8)) { 
 //BA.debugLineNum = 531;BA.debugLine="If PanelMain.Visible = False Then";
if (mostCurrent._panelmain.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 532;BA.debugLine="Linform.Visible = True";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 534;BA.debugLine="Linform.Visible = False";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 536;BA.debugLine="If	Starter.Cuenta > 0 And Starter.esperapelin =";
if (mostCurrent._starter._cuenta /*int*/ >0 && mostCurrent._starter._esperapelin /*int*/ ==7) { 
 //BA.debugLineNum = 537;BA.debugLine="Linform.Text =  Starter.inform & \"  ALL OK \"";
mostCurrent._linform.setText(BA.ObjectToCharSequence(mostCurrent._starter._inform /*String*/ +"  ALL OK "));
 }else {
 //BA.debugLineNum = 539;BA.debugLine="Linform.Text =  Starter.inform";
mostCurrent._linform.setText(BA.ObjectToCharSequence(mostCurrent._starter._inform /*String*/ ));
 };
 }else {
 //BA.debugLineNum = 542;BA.debugLine="Linform.Visible = False";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 546;BA.debugLine="If (Starter.EsperaMsg > 8)   Then	'NO hay DATOS -";
if ((mostCurrent._starter._esperamsg /*int*/ >8)) { 
 //BA.debugLineNum = 547;BA.debugLine="If PanelMain.Visible = False Then";
if (mostCurrent._panelmain.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 548;BA.debugLine="Linform.Visible = True";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 550;BA.debugLine="Linform.Visible = False";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 554;BA.debugLine="LtempIR.Text = \"-\"";
mostCurrent._ltempir.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 555;BA.debugLine="Label3.Text = \"-\"";
mostCurrent._label3.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 556;BA.debugLine="Ltsensor.Text = \"-\"";
mostCurrent._ltsensor.setText(BA.ObjectToCharSequence("-"));
 //BA.debugLineNum = 558;BA.debugLine="If (Starter.EsperaMsg < 20)   Then";
if ((mostCurrent._starter._esperamsg /*int*/ <20)) { 
 //BA.debugLineNum = 559;BA.debugLine="Label5.Text = DateTime.Time(DateTime.Now) & \" N";
mostCurrent._label5.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.DateTime.Time(anywheresoftware.b4a.keywords.Common.DateTime.getNow())+" NO Data."));
 //BA.debugLineNum = 560;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 561;BA.debugLine="PanelMain.Visible = False";
mostCurrent._panelmain.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 562;BA.debugLine="ListView1.Visible = False";
mostCurrent._listview1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 563;BA.debugLine="AvisoSensorFound = True";
_avisosensorfound = anywheresoftware.b4a.keywords.Common.True;
 };
 }else {
 //BA.debugLineNum = 566;BA.debugLine="If Starter.DatoBle Then								'Si es BT";
if (mostCurrent._starter._datoble /*boolean*/ ) { 
 //BA.debugLineNum = 567;BA.debugLine="If scaner = True And Starter.nWr < 145 Then";
if (_scaner==anywheresoftware.b4a.keywords.Common.True && mostCurrent._starter._nwr /*int*/ <145) { 
 //BA.debugLineNum = 568;BA.debugLine="Starter.inform =  \"  Point \" & Starter.secuenc";
mostCurrent._starter._inform /*String*/  = "  Point "+BA.NumberToString(mostCurrent._starter._secuencia /*int*/ )+"   Time: "+BA.NumberToString(mostCurrent._starter._segscan /*int*/ )+" s";
 //BA.debugLineNum = 569;BA.debugLine="Linform.Visible = True";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 571;BA.debugLine="If Starter.esperapelin > 14 Then";
if (mostCurrent._starter._esperapelin /*int*/ >14) { 
 //BA.debugLineNum = 572;BA.debugLine="Linform.Visible = False";
mostCurrent._linform.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 578;BA.debugLine="Activity.Title = \"TESS P \" & Starter.NameSensor";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("TESS P "+mostCurrent._starter._namesensor /*String*/ ));
 //BA.debugLineNum = 580;BA.debugLine="If scaner = True Then";
if (_scaner==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 581;BA.debugLine="Lorientacion.Text = \"Al \" &  Round2 ( Starter.";
mostCurrent._lorientacion.setText(BA.ObjectToCharSequence("Al "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._altura /*float*/ ,(int) (0)))+"   Az "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._azimut /*float*/ ,(int) (0)))));
 }else {
 //BA.debugLineNum = 583;BA.debugLine="Lorientacion.Text = \"Al \" &  Round2 ( Starter.";
mostCurrent._lorientacion.setText(BA.ObjectToCharSequence("Al "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._alturaacel /*float*/ ,(int) (0)))+"   Az "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._azimutacel /*float*/ ,(int) (0)))));
 };
 //BA.debugLineNum = 587;BA.debugLine="Label6.Text = \"Rx BT: \" & Starter.Cuenta";
mostCurrent._label6.setText(BA.ObjectToCharSequence("Rx BT: "+BA.NumberToString(mostCurrent._starter._cuenta /*int*/ )));
 //BA.debugLineNum = 589;BA.debugLine="If Starter.Es155espera = 0	Then";
if (mostCurrent._starter._es155espera /*int*/ ==0) { 
 //BA.debugLineNum = 590;BA.debugLine="Label3.Text = \"Mag: \" & Round2 (Starter.Magnit";
mostCurrent._label3.setText(BA.ObjectToCharSequence("Mag: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._magnitud /*float*/ ,(int) (2)))));
 }else {
 //BA.debugLineNum = 592;BA.debugLine="Label3.Text = \"Mag: \" & Round2 (Starter.Magnit";
mostCurrent._label3.setText(BA.ObjectToCharSequence("Mag: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._magnitud /*float*/ ,(int) (2)))+" *"+BA.NumberToString(mostCurrent._starter._es155espera /*int*/ )));
 };
 //BA.debugLineNum = 594;BA.debugLine="Ltsensor.Text = \"Tsens: \" & Round2 ( Starter.TA";
mostCurrent._ltsensor.setText(BA.ObjectToCharSequence("Tsens: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._tamb /*float*/ ,(int) (2)))));
 //BA.debugLineNum = 595;BA.debugLine="LtempIR.Text = \"T IR: \" & Round2 ( Starter.TObj";
mostCurrent._ltempir.setText(BA.ObjectToCharSequence("T IR: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._tobj /*float*/ ,(int) (2)))));
 //BA.debugLineNum = 596;BA.debugLine="If Starter.Vbat > 0 And Starter.Vbat < 500 Then";
if (mostCurrent._starter._vbat /*float*/ >0 && mostCurrent._starter._vbat /*float*/ <500) { 
 //BA.debugLineNum = 597;BA.debugLine="batery = Starter.Vbat ' tension diodo + v";
_batery = mostCurrent._starter._vbat /*float*/ ;
 //BA.debugLineNum = 598;BA.debugLine="If batery < 4.3 Then";
if (_batery<4.3) { 
 //BA.debugLineNum = 599;BA.debugLine="Lvbat.Text = \"Bat: \" & Round2 (batery,2)  & \"";
mostCurrent._lvbat.setText(BA.ObjectToCharSequence("Bat: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_batery,(int) (2)))+"v"));
 //BA.debugLineNum = 600;BA.debugLine="If batery > 3.9 Then";
if (_batery>3.9) { 
 //BA.debugLineNum = 601;BA.debugLine="Lvbat.TextColor = Colors.Green";
mostCurrent._lvbat.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 }else if(_batery>3.7) { 
 //BA.debugLineNum = 603;BA.debugLine="Lvbat.TextColor = Colors.LightGray";
mostCurrent._lvbat.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 }else if(_batery<3.7) { 
 //BA.debugLineNum = 605;BA.debugLine="Lvbat.TextColor = Colors.Red";
mostCurrent._lvbat.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 606;BA.debugLine="Label5.TextColor = Colors.Red";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 608;BA.debugLine="Label5.Text = \"LOW BATERY \" &  Round2 (bater";
mostCurrent._label5.setText(BA.ObjectToCharSequence("LOW BATERY "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_batery,(int) (2)))+"v"));
 };
 }else {
 //BA.debugLineNum = 611;BA.debugLine="Lvbat.Text = \"Charged\"";
mostCurrent._lvbat.setText(BA.ObjectToCharSequence("Charged"));
 };
 }else {
 //BA.debugLineNum = 614;BA.debugLine="Lvbat.TextColor = Colors.Black";
mostCurrent._lvbat.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 };
 //BA.debugLineNum = 617;BA.debugLine="If Starter.TObj > 0 Then";
if (mostCurrent._starter._tobj /*float*/ >0) { 
 //BA.debugLineNum = 618;BA.debugLine="LtempIR.TextColor = Colors.Red";
mostCurrent._ltempir.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 }else {
 //BA.debugLineNum = 620;BA.debugLine="LtempIR.TextColor = Colors.Green";
mostCurrent._ltempir.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 };
 //BA.debugLineNum = 623;BA.debugLine="If Starter.Magnitud = 24 Then";
if (mostCurrent._starter._magnitud /*float*/ ==24) { 
 //BA.debugLineNum = 624;BA.debugLine="Label3.TextColor = Colors.Red";
mostCurrent._label3.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 }else {
 //BA.debugLineNum = 626;BA.debugLine="Label3.TextColor = Colors.Green";
mostCurrent._label3.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 };
 }else {
 //BA.debugLineNum = 633;BA.debugLine="Label9.Text = \"IP: \" & Starter.dirurlSelec";
mostCurrent._label9.setText(BA.ObjectToCharSequence("IP: "+mostCurrent._starter._dirurlselec /*String*/ ));
 //BA.debugLineNum = 634;BA.debugLine="Label6.Text =  \"Rx WF: \" & Starter.CuentaUDP &";
mostCurrent._label6.setText(BA.ObjectToCharSequence("Rx WF: "+BA.NumberToString(mostCurrent._starter._cuentaudp /*int*/ )+"  +"+BA.NumberToString(mostCurrent._starter._udplost /*int*/ )));
 //BA.debugLineNum = 635;BA.debugLine="Lorientacion.Text = \"Al \" & Round2 ( Starter.Al";
mostCurrent._lorientacion.setText(BA.ObjectToCharSequence("Al "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._altura /*float*/ ,(int) (0)))+"   Az "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._azimut /*float*/ ,(int) (0)))));
 //BA.debugLineNum = 636;BA.debugLine="Label3.Text = \"Mag: \" & Round2 (Starter.Magnitu";
mostCurrent._label3.setText(BA.ObjectToCharSequence("Mag: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._magnitud /*float*/ ,(int) (2)))));
 //BA.debugLineNum = 637;BA.debugLine="Ltsensor.Text = \"Tsens: \" & Round2 ( Starter.TA";
mostCurrent._ltsensor.setText(BA.ObjectToCharSequence("Tsens: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._tamb /*float*/ ,(int) (2)))));
 //BA.debugLineNum = 638;BA.debugLine="LtempIR.Text = \"T IR: \" & Round2 ( Starter.TObj";
mostCurrent._ltempir.setText(BA.ObjectToCharSequence("T IR: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(mostCurrent._starter._tobj /*float*/ ,(int) (2)))));
 };
 //BA.debugLineNum = 641;BA.debugLine="If (Starter.Magnitud > 5) And (Starter.Magnitud";
if ((mostCurrent._starter._magnitud /*float*/ >5) && (mostCurrent._starter._magnitud /*float*/ <24)) { 
 //BA.debugLineNum = 642;BA.debugLine="Lnelm.Text = \"NELM: \" & Round2( MagnitudVisual(";
mostCurrent._lnelm.setText(BA.ObjectToCharSequence("NELM: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round2(_magnitudvisual(mostCurrent._starter._magnitud /*float*/ ),(int) (1)))));
 }else {
 //BA.debugLineNum = 644;BA.debugLine="Lnelm.Text = \"NELM: \"";
mostCurrent._lnelm.setText(BA.ObjectToCharSequence("NELM: "));
 };
 //BA.debugLineNum = 647;BA.debugLine="Starter.CuentaSg = Starter.CuentaSg + 1";
mostCurrent._starter._cuentasg /*int*/  = (int) (mostCurrent._starter._cuentasg /*int*/ +1);
 //BA.debugLineNum = 648;BA.debugLine="If Starter.CuentaSg > 60 Then";
if (mostCurrent._starter._cuentasg /*int*/ >60) { 
 //BA.debugLineNum = 649;BA.debugLine="Starter.CuentaSg = 0";
mostCurrent._starter._cuentasg /*int*/  = (int) (0);
 };
 };
 //BA.debugLineNum = 655;BA.debugLine="If Starter.OtrosBT Then";
if (mostCurrent._starter._otrosbt /*boolean*/ ) { 
 //BA.debugLineNum = 656;BA.debugLine="Starter.OtrosBT = False";
mostCurrent._starter._otrosbt /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 657;BA.debugLine="Label9.Text =  \"Other BT: \"  & Starter.NameSenso";
mostCurrent._label9.setText(BA.ObjectToCharSequence("Other BT: "+mostCurrent._starter._namesensorrxotro /*String*/ ));
 };
 //BA.debugLineNum = 659;BA.debugLine="If Starter.OtrosUdp Then";
if (mostCurrent._starter._otrosudp /*boolean*/ ) { 
 //BA.debugLineNum = 660;BA.debugLine="Starter.OtrosUdp = False";
mostCurrent._starter._otrosudp /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 661;BA.debugLine="Label9.Text =  \"Other W: \"  & Starter.NameSensor";
mostCurrent._label9.setText(BA.ObjectToCharSequence("Other W: "+mostCurrent._starter._namesensorrxotro /*String*/ +" "+mostCurrent._starter._dirurl /*String*/ ));
 };
 //BA.debugLineNum = 665;BA.debugLine="If Starter.MyTess = True Then";
if (mostCurrent._starter._mytess /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 666;BA.debugLine="If Starter.DatoBle = True Then";
if (mostCurrent._starter._datoble /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 667;BA.debugLine="Bmqtt.Visible = True";
mostCurrent._bmqtt.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 669;BA.debugLine="Bmqtt.Visible = False";
mostCurrent._bmqtt.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 672;BA.debugLine="If Starter.DatoBle = False Then";
if (mostCurrent._starter._datoble /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 673;BA.debugLine="Bmqtt.Visible = True";
mostCurrent._bmqtt.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 677;BA.debugLine="If Starter.FicheroCreado Then";
if (mostCurrent._starter._ficherocreado /*boolean*/ ) { 
 //BA.debugLineNum = 678;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 679;BA.debugLine="Label5.Text =  \" File: \"  & Starter.nombreFich &";
mostCurrent._label5.setText(BA.ObjectToCharSequence(" File: "+mostCurrent._starter._nombrefich /*String*/ +" Folder: "+_rpcfg.GetSafeDirDefaultExternal("")));
 //BA.debugLineNum = 680;BA.debugLine="Label5.Text =  \"File: \"   & rpcfg.GetSafeDirDefa";
mostCurrent._label5.setText(BA.ObjectToCharSequence("File: "+_rpcfg.GetSafeDirDefaultExternal("")+"/"+mostCurrent._starter._nombrefich /*String*/ ));
 //BA.debugLineNum = 681;BA.debugLine="Starter.FicheroCreado = False";
mostCurrent._starter._ficherocreado /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 684;BA.debugLine="If Starter.FicheroCreadoTodo Then";
if (mostCurrent._starter._ficherocreadotodo /*boolean*/ ) { 
 //BA.debugLineNum = 685;BA.debugLine="Label5.TextColor = Colors.White";
mostCurrent._label5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 686;BA.debugLine="Label5.Text =  \"File: \"  & Starter.nombreFichTod";
mostCurrent._label5.setText(BA.ObjectToCharSequence("File: "+mostCurrent._starter._nombrefichtodo /*String*/ +" Folder: "+_rpcfg.GetSafeDirDefaultExternal("")));
 //BA.debugLineNum = 687;BA.debugLine="Label5.Text =  \"File: \"  & rpcfg.GetSafeDirDefau";
mostCurrent._label5.setText(BA.ObjectToCharSequence("File: "+_rpcfg.GetSafeDirDefaultExternal("")+"/"+mostCurrent._starter._nombrefichtodo /*String*/ ));
 //BA.debugLineNum = 688;BA.debugLine="Starter.FicheroCreadoTodo = False";
mostCurrent._starter._ficherocreadotodo /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 691;BA.debugLine="If Starter.AlarmIR = True Then";
if (mostCurrent._starter._alarmir /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 692;BA.debugLine="BAlarmaIR.TextColor = Colors.Red";
mostCurrent._balarmair.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 };
 //BA.debugLineNum = 694;BA.debugLine="If Starter.AlarmMV = True Then";
if (mostCurrent._starter._alarmmv /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 695;BA.debugLine="BAlarmaMV.TextColor = Colors.Red";
mostCurrent._balarmamv.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 };
 //BA.debugLineNum = 698;BA.debugLine="If Starter.salvandoScan = False Then";
if (mostCurrent._starter._salvandoscan /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 699;BA.debugLine="BstartTas.Text = \"START\"";
mostCurrent._bstarttas.setText(BA.ObjectToCharSequence("START"));
 }else {
 //BA.debugLineNum = 702;BA.debugLine="BstartTas.Text = \"STOP\"";
mostCurrent._bstarttas.setText(BA.ObjectToCharSequence("STOP"));
 };
 //BA.debugLineNum = 704;BA.debugLine="If Starter.secuencia = 2 Then";
if (mostCurrent._starter._secuencia /*int*/ ==2) { 
 //BA.debugLineNum = 705;BA.debugLine="BstartTas.TextColor = Colors.Green";
mostCurrent._bstarttas.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 706;BA.debugLine="BstartTas.Enabled = True";
mostCurrent._bstarttas.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 713;BA.debugLine="End Sub";
return "";
}
public static String  _timboton_tick() throws Exception{
 //BA.debugLineNum = 829;BA.debugLine="Sub TimBoton_Tick  ' Timer para no repetir intento";
 //BA.debugLineNum = 830;BA.debugLine="TimBoton.Enabled = False";
_timboton.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 831;BA.debugLine="Bmqtt.TextColor = Colors.Gray";
mostCurrent._bmqtt.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 //BA.debugLineNum = 834;BA.debugLine="End Sub";
return "";
}
}
