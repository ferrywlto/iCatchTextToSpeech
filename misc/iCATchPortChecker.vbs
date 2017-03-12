' Get object 
Set objFSO = CreateObject("Scripting.FileSystemObject") 
Set objShell = CreateObject("Wscript.Shell") 
Set objNet = CreateObject("WScript.Network") 
 
' Define Objects 
strComputer = objNet.ComputerName 
StrDomain = objnet.UserDomain 
StrUser = objNet.UserName 
 
' ------ Set Constants --------- 
Const ForWriting = 2 
Const HKEY_LOCAL_MACHINE = &H80000002 
Const SEARCH_KEY = "DigitalProductID" 
 
' ------ Set Variables --------- 
Set objLogFile = objFSO.CreateTextFile(".\Port.txt", ForWriting, True) 
Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\CIMV2") 
 
On Error Resume Next 
Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\cimv2") 
Set colItems = objWMIService.ExecQuery("Select * from Win32_PnPEntity ") 
For Each objItem in colItems 
    If InStr(objItem.Name, "(COM") <> 0 Then
		Message = Message & "- " & objItem.Name & vbCrLf     
	End If	
Next 
 
objLogFile.Write Message 
objLogFile.WriteLine 
objLogFile.Close 
 
 
' Initialize title text. 
Title = "i*CATch Port Checker -  eToy Lab" 
 
objShell.Popup  Message,, Title, vbInformation + vbOKOnly