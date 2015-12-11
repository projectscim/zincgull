# Introduction #

This is how you create a certificate, and then export the .class-files to a single .jar-file that's possible to run on a website.


# Details #

```
...\zincgull>keytool -genkey -keyalg rsa -alias zincgull
Ange keystore-lösenord: asdfgh
Vad heter du i för- och efternamn?
  [Unknown]:  Zincgull Developers
Vad heter din avdelning inom organisationen?
  [Unknown]:  Development
Vad heter din organisation?
  [Unknown]:  The Zincgull Project
Vad heter din ort eller plats?
  [Unknown]:  Gothenburg
Vad heter ditt land eller din provins?
  [Unknown]:  Sweden
Vilken är den tvåställiga landskoden?
  [Unknown]:  SE
är CN=Zincgull Developers, OU=Development, O=The Zincgull Project, L=Gothenburg, ST=Sweden, C=SE korrekt?
  [nej]:  ja

Ange nyckellösenord för <zincgull>
        (RETURN om det är identiskt med keystore-lösenordet):

...\zincgull>keytool -export -storepass asdfgh -alias zincgull -file src\www\zincgull.crt
Ange keystore-lösenord:
Certifikatet har lagrats i filen <src\www\zincgull.crt>

...\zincgull>cd bin

...\zincgull\bin>jar -cvf ..\src\www\Zincgull.jar client\*.class
extra manifestfil
lägger till: client/Chat$1.class (in = 837) (ut = 499) (40% komprimerat)
lägger till: client/Chat.class (in = 4485) (ut = 2469) (44% komprimerat)
lägger till: client/GameArea.class (in = 7150) (ut = 3622) (49% komprimerat)
lägger till: client/PansyTadpole.class (in = 2448) (ut = 1208) (50% komprimerat)
lägger till: client/Player.class (in = 1160) (ut = 705) (39% komprimerat)
lägger till: client/Sidebar.class (in = 991) (ut = 615) (37% komprimerat)

...\zincgull\bin>cd ..\src\www

...\zincgull\src\www>jarsigner -storepass asdfgh Zincgull.jar zincgull

Warning:
The signer certificate will expire within six months.
```