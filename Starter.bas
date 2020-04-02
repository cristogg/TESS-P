B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=8
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Public SERVICE_UUID  As String = "6e400001-b5a3-f393-e0a9-e50e24dcca9e" 'Puerto serie
	Public CHARACTERISTIC_UUID_RX  As String = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
	Public CHARACTERISTIC_UUID_TX  As String = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"

	Public gps_permiso_ok As Boolean
	Dim ble As BleManager2
	Public ble_permiso_ok As Boolean
'	Private ble As BleManager2
	Public rp As RuntimePermissions
	
	Dim EstadoCBsabe As Boolean
	Dim udpRx As Int
	Dim nWr As Int
	Dim ContadorR As Int
	Dim Cuenta As Int
	Dim CuentaUDP As Int
	Dim CuentaSend As Int
	Dim CuentaSg As Int
	Dim TiempoSalvarSg As Int
	
	Dim Tim1sServ As Timer
	
	Dim raf As RandomAccessFile
	
	Dim Lat As String
	Dim Lon As String
	Dim Alt As Float 'String
	Dim DatoBle As Boolean
	Dim IdMac As String
	
	
	Dim TAmb, TObj, Frecuencia, Magnitud, Altura, Azimut As Float
	Dim AlturaAcel, AzimutAcel As Float
	Dim TAmbM, TObjM, FrecuenciaM, MagnitudM, AlturaM, AzimutM  As Float
	
	Dim Map1 As Map
	Dim MapBT As Map
	Dim msg As String
	
	Dim tsp_cfg As RandomAccessFile
	Dim rawrx As RandomAccessFile
	Dim NameSensor As String
	Dim NameSensorRx As String
	Dim ZPoint As Float
	Dim espera As Int
	
	Dim EsperaMsg As Int
	Dim EsperaSalvar As Int

'	Dim Tim1sSer As Timer
	Dim GPS1 As GPS
	Dim RxByBT As String
	
'	Dim udpsocket1 As UDPSocket
	Dim dirurl As String
	Dim dirurlSelec As String
	Dim MyTess As Boolean
	Dim OtrosBT As Boolean
	Dim OtrosUdp As Boolean
	
	Dim Lugar As String = "site"
	
	Dim FicheroCreado As Boolean
	Dim FicheroCreadoTodo As Boolean
	Dim nombreFich As String = "hola.txt"
	Dim NameFile As String
	Dim nombreFichTodo As String 
	Dim NameSensorRxOtro As String
'	Dim Layaut As Int
	Dim Tescaneo As Int
	Dim bep As Beeper
	Dim bepEnd As Beeper
	Dim	ContaBT As Int
	Dim	ContaRxScan As Int
	Dim	Vbat As Float
	
	Dim	SeqUdp As Int
	Dim	SeqUdpOld As Int
	Dim	UdpLost As Int
	
	Dim FlagAlarmIR As Boolean
	Dim FlagAlarmMV As Boolean
	Dim AlarmIR As Boolean
	Dim AlarmMV As Boolean
	Dim PeriodoAlarmaAcustica As Int
	
	Dim RefAlarmIR As Float
	Dim RefAlarmMV As Float
	
'	Dim rpcfg2 As RuntimePermissions
	Dim notification As Notification
	Dim inform As String = "Init"

	Dim pws As PhoneWakeState
	
	Public salvandoScan As Boolean
	Public CBsalvar As Boolean
	Dim nlinea As Int
	Public secuencia As Int
	Public RxCount As Int
		
	Public arrancado As Boolean
	Public esperapelin As Int
	Private ConnectedServices As List
	Public ConnectedName As String
	Public manager As BleManager2
	Public currentStateText As String = "UNKNOWN"
	Public currentState As Int
	Public BTrssi As Int
	Public connected As Boolean = False
	Public Vbat As Float
	Public DeltaIR As Float
	Public DeltaMag As Float
	
	Public SegScan As Int = 0
	Public Es155espera As Int = 0
End Sub

Sub Service_Create
	manager.Initialize("manager")
		
	arrancado = False
	esperapelin = 0

'	gps_permiso_ok = False
'	ble_permiso_ok = False
'
'Log("service start")
'	If ble_permiso_ok = True Then
''		ble_permiso_ok = False
'		ble.Initialize("ble")
'Log("ini ble")
'	End If
'	If gps_permiso_ok = True Then
''		gps_permiso_ok = False
'		StartGPS
'		udpsocket1.Initialize("UDP", 2255, 1000)
'Log("ini udp")
'	End If	
	
'	ble.Initialize("ble")
	TiempoSalvarSg = 10
	Lat = 0
	Lon = 0
	Alt = 0
	EsperaMsg = 0	
	DeltaIR = 2.0
	DeltaMag = 0.5
'	tsp_cfg.Initialize(File.DirRootExternal, "tsp_cfg.txt", False)
	Map1.Initialize
	MapBT.Initialize
'	DatoBle = True
	Tim1sServ.Initialize("Tim1sServ",1000)
' udpsocket1.Initialize("UDP", 2255, 1000)
	Tim1sServ.Enabled=True
	bep.Initialize(100,800)
	bepEnd.Initialize(200,600)
	'StartGPS
	Log("service create")

	Vbat = 500
End Sub




Sub CreateNotification (Body As String) As Notification
	
	notification.Initialize2(notification.IMPORTANCE_LOW)
	notification.Icon = "icon.png"
	
'	If alarm = True  Then
'		inform = "Alam ON"
'	End If
	If (EsperaMsg >10) Then
		inform = "NO sensor found."
	End If
	
	notification.SetInfo("TESS P", inform, Main)
	
	Return notification
End Sub


Sub Service_Start (StartingIntent As Intent)
	Service.StartForeground(1, CreateNotification("..."))
End Sub



public Sub StartGPS
	GPS1.Initialize("GPS")
	GPS1.Start(0, 0) 'Listen to GPS with no filters
End Sub

Sub GPS_LocationChanged (Location1 As Location)
	Lat =  Location1.ConvertToSeconds(Location1.Latitude)
	Lon =  Location1.ConvertToSeconds(Location1.Longitude)
'	Lon = Location1.ConvertToSeconds
	Alt =  Location1.Altitude
'	Log(Alt)
End Sub



'Return true to allow the OS default exceptions handler to handle the uncaught exception.
Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub


Sub Service_Destroy
	If Main.udpsocket1.IsInitialized Then
		Main.udpsocket1.Close
	End If
	manager.Disconnect
End Sub


'------

Public Sub ReadData
	For Each s As String In ConnectedServices
'		Log (s)
		If s = SERVICE_UUID Then
'			Log (" s ok")
			Try
				manager.SetNotify (s, CHARACTERISTIC_UUID_RX, True)
			Catch
				Log(LastException)
				Log (" Notify Exception")
			End Try			
		End If
	Next
End Sub



Public Sub Disconnect
	manager.Disconnect
	Manager_Disconnected
End Sub



Sub Manager_StateChanged (State As Int)
	Select State
		Case manager.STATE_POWERED_OFF
			currentStateText = "POWERED OFF"
		Case manager.STATE_POWERED_ON
			currentStateText = "POWERED ON"
		Case manager.STATE_UNSUPPORTED
			currentStateText = "UNSUPPORTED"
	End Select
	currentState = State
	inform = "BT " & currentStateText
'	CallSub(Main, "StateChanged")
End Sub



Sub Manager_DeviceFound (Name As String, Id As String, AdvertisingData As Map, RSSI As Double)
	'Log("Found: " & Name & ", " & Id & ", RSSI = " & RSSI & ", " & AdvertisingData & "  nsens: " & NameSensor) 'ignore
'	Log("Found: " & Name &  "  nsens: " & NameSensor) 'ignore
	
	IdMac = Id
	
'	If NameSensor.Contains( Name ) Then
'	Log("Substr  " & NameSensor.SubString2(0,6))
	
	Try
		If Name.Contains(NameSensor.SubString2(0,6)) Then
			BTrssi = RSSI
			inform = "Found " & Name
			Log (inform)
			ConnectedName = Name
			manager.StopScan
			
'		manager.Connect2(Id, False) 'disabling auto connect can make the connection quicker
			manager.Connect2(Id, True) 'disabling auto connect can make the connection quicker
		End If
	Catch
		Log(LastException)
	End Try
	

End Sub



Public Sub StartScan
	Private uuids As List
	
	inform = esperapelin & " State "
	uuids.Initialize
	uuids.Add(SERVICE_UUID)
	If manager.State <> manager.STATE_POWERED_ON Then		
		inform = inform & "BT OFF."
		Log(inform)
	Else If rp.Check(rp.PERMISSION_ACCESS_COARSE_LOCATION) = False Then
		Log("No location permission.")
	Else
		manager.Scan2(Null, False)
		inform = inform & " BT ON."
		Log(inform)
'manager.Scan(uuids)
	End If
End Sub

Sub Manager_DataAvailable (ServiceId As String, Characteristics As Map)
	RxCount = RxCount+1	

	CallSub3(Main, "DataAvailable", ServiceId, Characteristics)

	For Each id As String In Characteristics.Keys
		If id = CHARACTERISTIC_UUID_RX Then 	
	'		CallSub(Main, "CreateCharacteristicItem")
			CreateCharacteristicItemStar(id, Characteristics.Get(id))
'Log("Data Avalilable")			
		End If
	Next
End Sub


'viene de main

Sub CreateCharacteristicItemStar(Id As String, Data() As Byte) As Panel
	Dim p As JSONParser
	Dim nserie As String
'Log(   BytesToString(Data, 0, Data.Length, "UTF8"))	
	p.Initialize(BytesToString(Data, 0, Data.Length, "UTF8"))
'	Map1 = p.NextObject
	Try
		Map1 = p.NextObject
		secuencia = Map1.Get("seq")
'		Altura = Map1.Get("alt")
'		Azimut = Map1.Get("azi")
		TObj = Map1.Get("tsky")
		TAmb = Map1.Get("tamb")
		Magnitud =Map1.Get("mag")
		Frecuencia = Map1.Get("freq")
	Catch
		Log("nastic")
		inform = (" Error cadena json.")
	End Try
	
	Try	
		Vbat = Map1.Get("vbat")
	Catch
		Vbat = 500
	End Try


	Try
		Altura = Map1.Get("alt")
		Azimut = Map1.Get("azi")
	Catch
		Altura = 0
		Azimut = 0
	End Try


	Try
		AlturaAcel = Map1.Get("ala")
		AzimutAcel = Map1.Get("azm")
	Catch
		AlturaAcel = 0
		AzimutAcel = 0
	End Try



	If secuencia = 0 Then  'En los barridos TAS empieza por cero
		nWr = 0
		salvandoScan = True
		NameFile =  "AS_" & DateTime.Date(DateTime.Now) & "_" & DateTime.Time(DateTime.Now)
	'	Log(NameFile)
		GuardaDato
		bep.Initialize(400,800)
		bep.Beep
	Else If secuencia < 145 And salvandoScan = True Then
		Es155espera = 0
		Main.scaner = True
		GuardaDato
		If secuencia = 144 Then
			salvandoScan = False
			Log("fin barrido")
			bep.Initialize(400,500)
			bep.Beep
		End If
	Else If secuencia = 200 Then
		salvandoScan = False
		Log("Fin scan")
		Es155espera = 0
	Else If secuencia = 155 Then
		Es155espera = Es155espera + 1
'		Log("Espera terminar scan")	
	Else
		Es155espera = 0
		Main.scaner = False
	End If
	
'	If secuencia > 200  Then				
'			Main.scaner = False
'		Else
'			Main.scaner = True				
'	End If
'	
			
	If (EstadoCBsabe = True)  Then
		ContadorR =  ContadorR + 1
		MagnitudM = MagnitudM + Magnitud
		FrecuenciaM = FrecuenciaM + Frecuencia
		TAmbM = TAmbM + TAmb
		TObjM = TObjM + TObj
	Else
		ContadorR = 1
		MagnitudM =  Magnitud
		TAmbM =  TAmb
		TObjM =  TObj
	End If

	
	DatoBle = True
	Cuenta = Cuenta + 1  'Cuenta total de rx BT	
	EsperaMsg= 0
End Sub

''


Sub Manager_Disconnected
	Log("Disconnected")
	inform = inform & " BT Disconnected"
	connected = False
	CallSub(Main, "StateChanged")
End Sub

Sub Manager_Connected (services As List)
	
	inform = esperapelin & " BT Connected"
	Log(inform)
'	connected = True
	ConnectedServices = services
	
'	If manager.RequestMtu(180) Then
	If manager.RequestMtu(200) Then
		connected = True
		'inform = esperapelin & " Connected " & "mtu ok"
		Log(" Connected " & "mtu ok")
	Else
		'inform = esperapelin & " Connected " & "mtu fail"
		Log( " Connected " & "mtu fail")
	End If
	CallSub(Main, "StateChanged")	
	'ReadData
End Sub
'-------





Sub GuardaDato			' Guarda Datos
	Dim nombre As String
	Dim comienzo As String

	nombre = NameSensor & "_" & NameFile & "_" & Lugar &  ".txt"  'el nombre del fichero se toma al inicio
	nombreFich = nombre
'	Log(nombreFich)
	
	If File.Exists(rp.GetSafeDirDefaultExternal(""), nombreFich) = True Then  		'Existe
		GuardaTrack
	Else
		RxCount = 0		
		comienzo = ""	'Para completar lista de variables, en lugar de fecha y hora se rellenan con NameSensor y el ZPoint
		comienzo = "# " & TAB & NameSensor & TAB & ZPoint & TAB & "T IR" & TAB & "T Sens" & TAB & "Mag  " & TAB & "Hz   "  & TAB
		comienzo = comienzo & "Alt" & TAB & "Azi  " & TAB & "Lat    " & TAB & "Lon   " & TAB & "SL m" &  CRLF
		Try
			File.WriteString(rp.GetSafeDirDefaultExternal(""), nombreFich, comienzo)
		Catch
			ToastMessageShow("Can't write file.", False)
			Log("Can't write file.")
		End Try
		'	espera = 0
			FicheroCreado = True
		
		GuardaTrack
	'	CallSub(Main, "CreateCharacteristicItem")

	End If
	
End Sub


'---------------------------------------------------------------------------------------------------------------------
'{"seq":1564, "rev":2, "name":"TASBC3", "freq":29411.77, "mag":9.03, "tamb":25.59, "tsky":24.17, "wdBm":0, "alt":0.00, "azi":0.00}
'---------------------------------------------------------------------------------------------------------------------
Sub GuardaTrack () ' Guarda una linea
	Dim pp As String
	Dim fecha As String
	
	If CBsalvar = True Then  'salvar lecturas sueltas / salvar scan
		nlinea = nlinea + 1
	Else
		nlinea = secuencia + 1
	End If
	
'	fecha = ContaRxScan & TAB & DateTime.Date(DateTime.Now) &" " & TAB & DateTime.Time(DateTime.Now)
	fecha = nlinea &" " & TAB & DateTime.Date(DateTime.Now) &" " & TAB & DateTime.Time(DateTime.Now)
	If nlinea > 200 Then  ' estamos en lecturas sueltas
		pp =  fecha & TAB & Round2(TObjM/ContadorR , 2) &TAB & Round2(TAmbM/ContadorR,2) &TAB & Round2(MagnitudM/ContadorR ,2) &TAB
		If Frecuencia < 100 Then
			'pp = pp & Round2(Frecuencia,3)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
			pp = pp & Round2(FrecuenciaM/ContadorR,3)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
		Else
			'pp = pp & Round2(Frecuencia,0)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
			pp = pp & Round2(FrecuenciaM/ContadorR,0)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
		End If
	Else		'es un scan
		pp =  fecha & TAB & Round2(TObj , 2) &TAB & Round2(TAmb,2) &TAB & Round2(Magnitud ,2) &TAB
		If Frecuencia < 100 Then
			pp = pp & Round2(Frecuencia,3)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
		Else
			pp = pp & Round2(Frecuencia,0)& TAB  & Round2(Altura,0) & TAB & Round2(Azimut,0) & TAB
		End If		
	End If

	If GPS1.IsInitialized Then
		pp = pp & Lat & TAB  & Lon & TAB &  Round2(Alt,0) &  CRLF
	End If
	Log(pp)
	Try
		raf.Initialize(rp.GetSafeDirDefaultExternal(""), nombreFich, False)
		raf.WriteBytes(pp.GetBytes("Windows-1252"), 0, pp.Length, raf.Size)
	Catch
		ToastMessageShow("Can't write file.", False)
	End Try
	nWr = nWr + 1
	raf.Close

End Sub



Sub UDP_PacketArrived(Packet As UDPPacket)
	Dim NameSensorRx As String

'	Dim n As Notification = CreateNotification("hola")
'	n.Notify(1)

'	Main.Tim1s.Enabled=True
	msg = BytesToString(Packet.Data, Packet.Offset, Packet.Length, "UTF-8")
	'Label5.Text = msg
	Dim p As JSONParser
	p.Initialize(msg)
'	Tim1sServ.Enabled=True
	
	Map1 = p.NextObject
	NameSensorRx = Map1.Get("name")
	dirurl = Packet.HostAddress

	CuentaUDP = CuentaUDP + 1
	
	If (NameSensorRx = NameSensor) Then
'		Layaut = 2
		EsperaMsg = 0
		DatoBle = False
		If (NameSensorRx.StartsWith( "TESS-P") ) Then
			MyTess = True
		Else
			MyTess = False
		End If
'Log(msg)		
		dirurlSelec = dirurl
		Magnitud =  Map1.Get("mag")
		Frecuencia = Map1.Get("freq")
'		TAmb = Map1.Get("tamb")
'		TObj = Map1.Get("tsky")

		Try
			SeqUdp = Map1.Get("seq")
		Catch
'			Log("tamb")
			SeqUdp = Map1.Get("udp")
		End Try
		
		
		If (SeqUdp - SeqUdpOld ) > 1 Then
			UdpLost = UdpLost + 1			
		End If		
		SeqUdpOld = SeqUdp

		Try
			TAmb = Map1.Get("tamb")
			TObj = Map1.Get("tsky")
		Catch
'			Log("tamb")
			TAmb = 0
			TObj = 0
		End Try
	
'		Try
'			udpRx = Map1.Get("udp")
'		Catch
'			udpRx = 0
'		End Try

		
		Try			
'			udpRx = Map1.Get("udp")
			Altura = Map1.Get("alt")
			Azimut = Map1.Get("azi")
			ZPoint = Map1.Get("ZP")
		Catch
			Altura = 0
			Azimut = 0
			ZPoint = 0
'			udpRx = 0
		End Try
						
		If (EstadoCBsabe = True)  Then
			ContadorR =  ContadorR + 1
			FrecuenciaM = FrecuenciaM + Frecuencia
			MagnitudM = MagnitudM + Magnitud
			TAmbM = TAmbM + TAmb
			TObjM = TObjM + TObj
			AlturaM = AlturaM + Altura
			'AzimutM = AzimutM + Azimut 'Azimut no se puede promediar directamente, hay que hacerlo en polares
		Else
			ContadorR = 1
			FrecuenciaM = Frecuencia
			MagnitudM =  Magnitud
			TAmbM =  TAmb
			TObjM =  TObj
			AlturaM =  Altura
			AzimutM =  Azimut
			
		End If
		
	Else
		NameSensorRxOtro = NameSensorRx
		OtrosUdp = True
'		If  EstadoCBsabe = True Then
'			GuardaTodo(msg)
'		End If
		
	End If
		
End Sub




Public Sub ScaneaBT
'	Log("btnScan")
'	rp.CheckAndRequest(rp.PERMISSION_ACCESS_COARSE_LOCATION)
'	Wait For Activity_PermissionResult (Permission As String, Result As Boolean)
'	If Result = False Then Return

'	pbScan.Visible = True
	'CallSub(Starter, "StartScan")
	Log("ScanBT")	
	StartScan
'	inform = ("Starting Scan BT")
End Sub

'Sub Bscan
'	Log("btnScan")
'	rp.CheckAndRequest(rp.PERMISSION_ACCESS_COARSE_LOCATION)
'	Wait For Activity_PermissionResult (Permission As String, Result As Boolean)
'	If Result = False Then Return
'	'pbScan.Visible = True
'	StartScan
'	'CallSub(Starter, "StartScan")
'	
'End Sub





Sub Tim1sServ_Tick

	SegScan = SegScan +1
	
	esperapelin = esperapelin +1
	
	If gps_permiso_ok = True Then  'primera vez
		gps_permiso_ok = False
		StartGPS
		Main.udpsocket1.Initialize("UDP", 2255, 1000)
	End If
	
	If NameSensor.Contains("stars") Then
		Log("stars found")		
	Else	 		'  Es un TAS BT
		inform = esperapelin	
		If arrancado = False And esperapelin = 2 Then	
			EsperaMsg = 0
			inform = inform & ( " Scanin BT ")
			Log(inform)
			StartScan
		Else If arrancado = False And (esperapelin = 4 Or esperapelin = 5 Or esperapelin = 6)  And connected = True Then
			arrancado = True		
			inform = inform & (" Waiting Data ")
			Log(inform)
			ReadData
			
		End If

'		If ((EsperaMsg = 8) And (arrancado = True))   Then
		If (EsperaMsg > 10)   Then
			EsperaMsg = 0
			arrancado = False
			esperapelin = 0		
			connected = False
			inform = inform & ( " Waiting data > 8 sg")		
			Log(inform)
			
		End If	

		If (esperapelin > 6) And connected = False  Then
			arrancado = False
			EsperaMsg = 0
			esperapelin = 0	
			inform = inform & " Wait Sensor > 6 sg"
			Log (inform)
			manager.Disconnect ' desconecta para empezar de nuevo
		End If
						
	End If

	EsperaMsg = EsperaMsg + 1
	
	EsperaSalvar = EsperaSalvar + 1	
	espera = espera+1	
	
	If (EstadoCBsabe = True) And secuencia > 200   Then
		If (EsperaSalvar >= TiempoSalvarSg  ) Then
			EsperaSalvar = 0
			If (ContadorR > 0) Then
				GuardaDato
				ContadorR = 0
				TObjM = 0
				TAmbM = 0
				MagnitudM = 0
				FrecuenciaM = 0
				AlturaM = 0
			End If
		End If
		
		Tescaneo = Tescaneo +1
		
		If ((EsperaMsg > 20) And (EsperaMsg < 25))   Then ' avisa durante 10sg perdida señal
			If Main.udpsocket1.IsInitialized = False Then
'				Log("no ini")				
			Else
'				Log("si ini")	
			End If
			
			bep.Initialize(400,1000)
			bep.Beep			
		End If
		If (EsperaMsg > 25)   Then ' avisa durante 10sg perdida señal
			EsperaMsg = 10
		End If
		
		
	End If
	
'-----------Alarmas--------------	
	If FlagAlarmIR = True Then		
		If Abs( TObj - RefAlarmIR) > DeltaIR Then
			bep.Initialize(100,600)
			bep.Beep			
			AlarmIR = True
		Else
			AlarmIR = False
		End If				
	End If
	If FlagAlarmMV = True Then		
		If Abs(Magnitud - RefAlarmMV) > DeltaMag Then
			bep.Initialize(100,800)
			bep.Beep
			AlarmMV = True
		Else
			AlarmMV = False
		End If
	End If
	
	If Magnitud = 24 Then		' avisa si TAS esta guardado sin apagar.		
		PeriodoAlarmaAcustica = PeriodoAlarmaAcustica +1
		If PeriodoAlarmaAcustica > 4 Then
			bep.Initialize(600,2000)
			bep.Beep
			PeriodoAlarmaAcustica = 0
			Magnitud = 0
'			Log("al " & Magnitud)
		End If	
	End If
	If Vbat < 3.7 Then	' avisa de bateria baja
		PeriodoAlarmaAcustica = PeriodoAlarmaAcustica +1
		If PeriodoAlarmaAcustica > 4 Then
			bep.Initialize(200,1500)
			bep.Beep
			PeriodoAlarmaAcustica = 0
			Vbat = 500
'			Log("albat " & Vbat)
		End If
	End If
		

End Sub

