
A)
-Datele sunt luate de la "http://data.fixer.io/api/latest?access_key=11c01117d37a6d8def49765132700db5&format=1".
-Din pacate intoarce doar ratele de schimb vs EURO pt ziua curenta, nu se poate face query cu alte monede sau alte zile.
-N-am gasit altceva mai bun gratis.
-Anumite flag-uri lipsesc pentru ca nu le-am gasit poze.

 B & C
- Subpunctele B si C sunt in acelasi fragment. Graficul /lista se vor afisa la click pe butoanele respective.

- Pentru afisarea pe grafic, am gasit o librarie care nu merge excelent, uneori nu afiseaza toate valorile pe care i le dau.
- De asta cand selectati intervalul de timp,selectati un interval mai mare pentru ca sa arate mai decent pe grafic.

- Din cauza ca n-am gasit un api care sa imi intoarca datele pe mai multe zile, datele atat cele din lista cat si cele din grafic
sunt generate de mine random.
- Am pornit de la exchange value pentru ziua curenta, am stabilit un interval de variatiee +/- 10%, si am generat array-uri de valori de marimea numarului de zile selectat (endDate-startDate)
- La fiecare click pe unul din cele doua butoane, se vor genera alte valori (deci graficul/lista se vor modifica).

- Datele sunt salvate la generare in doua tabele, unul pt lista de min/max, celalalt pt grafic, cu cheia unica timestamp-ul cand s-a generat raportul


D)
- Baza de date am creat-o pe sdcard, ca sa fie mai simplu de luat cu adb pull. E un boolean in DBProvider.java, ce se poate schimba pt app context
- Sunt doua fragmente, unul contine lista rapoartelor (impartite in cele doua categorii: grafic respectiv lista de min/max).
- Celalalt fragment contine detaliul raportului, afisat tot asa fie grafic, fie lista de valori minime/maxime.


Alte specificatii:

- Este posibil ca pe emulator-ul Android Studio sa nu mearga (la mine nu a mers) , dar am rulat pe telefon si totul a fost oke.
- Posibil sa mai existe cateva bug-uri, este prima data cand fac o tema asa cu astfel de cerinte si oarecum contra timp.
- Ca dificultate a fost una mai grea pentru mine,dar sper ca indeplineste cerintele.




