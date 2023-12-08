# Tema 2 APD - Planificare Taskrui Java

## Descriere
Scopul temei este implementarea unui sistem de planificare a task-urilor intr-un datacenter, folosind Java Threads.  
Sistemul este compus din doua elemente principale. **Dispatcher-ul** este cel care preia task-urile ce sosesc in sistem si le trimite catre nodurile Host. **Host-urile** sunt cele care executa task-urile pe care le primesc. Fiecare **host** are cate o coada in care se stocheaza task-urile primite.  
**Dispatcher-ul** poate functiona cu una dintre cele 4 politici de planificare:
- ***Round Robin***:  
    Tascurile sunt alocate, pe masura ce vin, nodului (i+1)%n, n - nr Hosts, i - id nod
- ***Shortest Queue:***  
    **Dispatcher-ul** aloca un task **Host-ului** care are coada te taskuri in asteptare de dimensiune *minima*. Obs. Se tine in cont si taskul ce ruleaza pe **Host** in momentul calcularii dimensiunii cozii.
- ***Size Interval Task Assignement:***  
    Exista doar 3 **Host-uri** care executa task-uri. **Host-urile** sunt grupate dupa cele 3 categori de taskuri:
    *Scurte, Medii, Lungi*. Fiecare **Host** este specific unei categorii.
- ***Least Work Left:***  
    **Dispatcher-ul** aloca un task **Host-ului** care are durata totala de calcule ramase de executat *minima*.

Task-urile au urmatoarele proprietati:
- **ID** -> un numar intreg, unic, de la 0 la n-1 (n = nr total task-uri);
- **start Time** -> Momentul de timp la care task-ul intra in sistem;
- **durata** -> timpul necesar executiei acestui task
- **tipul** -> Tipul task-ului (Scurt, Mediu, Lung)
- **prioritatea** -> un numar intreg care defineste importanta task-ului.
- **preemptibilitatea** -> boolean ce specifica daca task-ul poate fi intrerupt.

## Implementare
### **Dispatcher**
#### Variabile:
- `taskNo` -> Nr de taskuri intrate in sistem Util pentru Round Robin
- `mutex` -> Semafor ce permite un singur thread. Util deoarece citirea se realizeaza de mai multe thread-uri.
#### addTask:
Se verifica ce tip de algoritm este folosit si se intra pe urmatoarele cazuri:
- ***Round Robin***: Trimit task-ul la **Host-ul** corect folosind formula (`taskNo`+1)%n, n = nr **Host-uri**. Apoi incrementez `taskNo`.
- ***Shortest Queue***: Caut **Host-ul** cu coada minima si trimit task-ul acolo.
- ***Size Interval Task Assignement***: Verific tipul task-ului si il trimit la **Host-ul** curent.
- ***Least Work Left***: Salvez work-ul minim si il verific cu urmatorul astfel: `minim - nextWorkLeft > 950`. Verific daca este mai mare de 950 milisecunde deoarece, pentru a fi o diferenta semnificativa intre **Host-uri** trebuie sa fie o secunda de munca ramasa. Astfel compensez si pentru timpul pierdut pe mutex si la intreruperea task-ului. Aest timp nu a depasit la testare 15 ms dar pentru a fi "safe" am ales 50 ms.

### **Host**
### Variable:
- `queue` -> Un PriorityBlockingQueue ordonat dupa prioritate, descrescator, apoi dupa timpul de start, crescator. Am ordonat si dupa timpul de start pentru a introduce task-urile intrerupte inapoi in acelasi queue.
- `task` -> Task-ul curent aflat in executie
- `isRunning` -> Un boolean care se face fals la inchiderea Host-ului

### addTask:
Adaug task-ul in coada.

### getQueueSize:
Returnez queue.size si daca exista un task in executie adaug 1.

### getWorkLeft:
Returnez suma milisecundelor atat task-urilor aflate in asteptare cat si task-ului aflat in executie.

### shutdown:
Golesc queue si fac `isRunning` False.

### run:
In aceasta metoda se executa task-urile. Tot se intampla intr-o bucla. Cat timp isRunning este true, inseamna ca Host-ul inca este in viata si trebuie sa se execute sau sa astepte task-uri. Daca exista task-uri in coada se extrage primul task si incepe executia. Se verifica la inceput daca exista un task cu prioritate mai mare in coada (asta daca task-ul curent este preemptibil) si daca da se introuce task-ul curent in coada si se opreste rularea task-ului curent. Daca nu se scade variabila `left` din interiorul task-ului, variabila ce reprezinta timpul ramas pentru executie. La final, daca task-ul a ramas fara timp ramas (aproape mereu ramane 0, am vazut o singura data sa fie -1 ms) se apeleaza `task.finish()` si se alege alt task.

## Rulare Tema
In fisierul checker, se executa checker.sh pentru a rula local.  
Daca masina beneficiaza de docker, se poate executa intr-un container de docker pentru a avea o rulare cat mai consistenta. 

