Tema2 APD - Cristache Cristian-Valentin

Pentru a implementa cerinta avem nevoie de 2 tipuri de taskuri si 2 pooluri.

Taskurile order se ocupa de procesarea comenzilor si crearea taskurilor de tip
product. Fiecare task are de citit o anumita linie din fisierul de comenzi.
Apoi initializeaza atatea taskuri product cate produse sunt in comanda.
Printr-un semafor, taskurile order asteapta ca taskurile product aferente 
sa se termine pentru a da shipped la o comanda. Semaforul incepe cu
1 - numarul de produse. Fiecare produs da release iar coamnda da aquire
ca sa poata continua. Deci trebuie ca toate produsele sa fie shipped ca sa
ajungem sa dam shipped la un order. 
Pentru a inchide cele doua executor services, retin numarul de comenzi 
intr-un AtomicInteger si le dau shutdown la cele doua in acelasi timp. 
Nu e nevoie de un contor separat pentru produse deoarece nu o sa se 
intample vreodata sa vrem sa oprim poolul de produse inaintea celui
de comenzi. Cele doua pooluri trebuie oprite cam in acelasi timp. Asa
evitam si ca poolul de produse sa se inchida prea devreme.
La inceput pun in poolul de orderuri P comenzi.

Taskurile product cauta al x-lea produs din comanda. Dupa ce il gaseste il
trece drept shipped si da realese semaforului. Fiecare task cauta un
produs cu alt numar deci nu vor exista threaduri care sa aleaga acelsi 
produs de 2 ori.
