# Programozási Technológia 2 - nagybeadandó

### Publikus funkciók:
#### Filmek listázása
A filmek összes tulajdonsága megjelenik egy táblázatban.
A táblázatban kattintással kijelölhető egy film. A képernyőn ekkor megjelenik a kijelölt film plakátjának a képe.
A kiválasztott filmhez kapcsolódó előadásokat is meg tudjuk jeleníteni az „List shows”gombra kattintva.
#### Előadások listázása
Egy táblázatban látható, hogy a kiválasztott filmet mikor vetítik.
Kijelölhető egy előadás, majd a „Book for selected show” gombra kattintva elindítható a foglalási folyamat.

#### Előadások szűrése
Film, terem vagy dátum szerint szűrhető a lista.
#### Helyfoglalás
A program grafikusan megjeleníti a képernyőn a terem széksorait. A szabad székeket zöld, a lefoglalt székeket piros szín jelöli. A helyfoglalásnál ki kell választani, hogy 1, 2 vagy 4 helyet szeretnénk egyszerre lefoglalni. 
A program automatikusan számolja a kijelölt székeket, és csak a választott menniségű széket enged kijelölni. A választást sárga színnel jelzi.
Korábban kijelölt székre kattintva a választás visszavonható, helyette másik hely választható.
Ha végeztünk a hely(ek) kiválasztásával, a „Book for selected seat(s)” gombra kattintva léphetünk a foglalás véglegesítéséhez.
Foglalás véglegesítése/visszavonása
Ezen a felületen visszavonható (piros „Cancel” gomb) vagy véglegesíthető ( zöld „Finalize” gomb) a foglalás.

### Személyzeti funkciók:
#### Új előadás meghirdetése
A film listából egy filmet kiválasztva a „Create new show” gombra kattintva érhető el az új előadás meghirdetésére szolgáló felület.
A kiválasztott filmhez meg kell adni a termet, a dátumot, és az időpontot.
A program figyel a következő korlátozásokra: 
-	Egy film nem vetíthető a megengedettnél több alkalommal. 
-	Egy film maximum 3 teremben vetíthető párhuzamosan. 
-	Egy terembe egyszerre csak egy előadás osztható be (a filmvetítés után fél óra takarítási idő van).
-	Az 1. korhatár besorolású filmek bármely időpontban vetíthetők, a 2. csak 17 óra után, a 3. csak 21 óra után.
Előadás meghirdetésekor segít a szűrő, pl. a megadott filmhez kiválasztott terem és dátum mellé csak az elérhető időpontokat listázza kiválaszthatóként.
#### Előadás törlése
Csak akkor törölhető egy előadás, ha senki sem foglalt rá helyet. Ellenkező esetben a rendszer ezt jelzi, és az előadás nem törölhető.

## Az adatbázisban tárolt adatok
### Film:
- cím
- gyártó ország
- szinkronizált-e
- rendező
- a tartalom leírása
- hossz
- maximum hányszor lehet lejátszani
- korhatár besorolás (1., 2., 3.)
- a film plakátjának a képe
### Terem: 
- név
- sorok és oszlopok száma
### Előadás: 
- film azonosító
- kezdő időpont
- terem azonosító
### Hely: 
- előadás azonosító
- terem azonosító
- sor
- oszlop
- státusz (foglalt-e)
