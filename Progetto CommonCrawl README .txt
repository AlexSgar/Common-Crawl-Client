La vista di sviluppo relativa all'applicazione da noi creata consta di 3 directory presenti all'interno della cartella src del progetto principale netbeans.

Queste cartelle sono rispettivamente:
1)file warc.path:questa directory contiene il file warc.path che memorizza tutti i segmenti (files .warc) in formato gzip dell'intero web. E' possibile scaricare tale file, relativo alla data in cui è stato effettuato il crawling, seguendo questo URL http://commoncrawl.org/the-data/get-started/ e scegliendo quindi lo snapshot del web nel periodo di interesse cliccando, come nell'esempio nostro, sul link http://commoncrawl.org/august-2014-crawl-data-available/ . Questo è lo snapshot del web effettuato nel Agosto 2014(il crawling del web totale). Scaricare il file https://aws-publicdatasets.s3.amazonaws.com/common-crawl/crawl-data/CC-MAIN-2014-35/warc.paths.gz il suo hyperlink nella pagina è "all WARC files" seguendolo scarica il file gzip che verrà decompresso in questa cartella, ottenendo il file warc.path.
2)file wat.path:questa directory contiene il file wat.path che memorizza tutti i segmenti (files .wat) in formato gzip dell'intero web. E' possibile scaricare tale file, relativo alla data in cui è stato effettuato il crawling, seguendo questo URL http://commoncrawl.org/the-data/get-started/ e scegliendo quindi lo snapshot del web nel periodo di interesse cliccando, come nell'esempio nostro, sul link http://commoncrawl.org/august-2014-crawl-data-available/ . Questo è lo snapshot del web effettuato nel Agosto 2014(il crawling del web totale). Scaricare il file https://aws-publicdatasets.s3.amazonaws.com/common-crawl/crawl-data/CC-MAIN-2014-35/wat.paths.gz il suo hyperlink nella pagina è "all WAT files" seguendolo scarica il file gzip che verrà decompresso in questa cartella, ottenendo il file wat.path.
3)file wat:questa directory contiene il file .wat (il segmento attuale) che l'applicazione, durante la creazione dell'indice, utilizzerà nella computazione. Ogni volta che viene scaricato un file .wat l'applicazione effettua il parsing riga per riga richiamando la funzione di creazione dell'indice, al termine dell'analisi del file(raggiunto l'end of file) tale file viene rimosso automaticamente e verrà quindi scaricato il successivo file .wat(i segmenti presenti nel file wat.path).
4)progettosii:questa directory contiene tutti i sorgenti dell'applicazione.

Le responsabilità funzionali dei vari sorgenti sono le seguenti:

1)ControllerIndex:scarica i file .wat presenti nel file wat.path, li decomprime e li analizza richiamando le relative classi responsabili del parsing di tali files al fine di creare l'indice. Al termine di ciascuna iterazione su ciascun file .wat tale file viene automaticamente eliminato.
2)ControllerRequest:riceve l'url di interesse in input e verifica, tramite la cache, se tale url è presente nel file .warc(gzip). Ritorna l'array di bytes relativo al corpo della pagina web puntata dall'url dato in input.
3)ConnectorDB:ritorna l'oggetto Connection utilizzato per la creazione dei statement SQL impiegati nell'applicazione.
4)EstrattoreJson:ritorna l'oggetto rappresentante la tupla da inserire nell'indice. Tale oggetto(ObjectURL) viene creato parsando la struttura JSON memorizzata all'interno del file .wat che si stà analizzando.
5)ParseWat:si occupa dell'inserimento delle tuple all'interno dell'indice. Tale inserimento è effettuato parsando il file .wat corrente. Il parsing consiste nella creazione di oggetti rappresentanti le singole tuple dell'indice attraverso l'analisi della struttura del file .wat corrente. Tale file ha una struttura ben definita che ci consente di estrarre la rappresentazione in JSON delle informazioni di interesse. Richiama EstrattoreJSON per creare tale oggetto e lo inserisce nel DB.
6)ObjectURL:rappresenta l'oggetto di interesse da inserire nell'indice.
7)TableManager:si occupa di creare sia il DB che le relative relazioni rappresentanti sia l'indice che la cache.
8)WarcReader:ritorna l'array di bytes rappresentante il contenuto della pagina puntata dall'url di interesse. Per estrarre questo array di bytes analizza il file .warc memorizzato nella cache.
9)Cache:si occupa di gestire la memorizzazione progressiva dei vari files .warc utilizzati per le operazioni di retrive del contenuto della pagina relativa all'url di interesse. Ad ogni invocazione del metodo getRequest della classe ControllerRequest la cache verifica la presenza, tramite la relazione nel DB cache, del file .warc che contiene la pagina puntata dall'url. Tramite un controllo sugli offset relativi a ciascun URL(l'offset rappresenta la posizione della pagina puntata dall'url all'interno del file .warc) la cache verifica se tale URL è gia presente nel file .warc oppure è necessario un ulteriore download di tale file per poterlo recuperare. Gestisce una politica di cancellazione dei file più vecchi nel caso in cui la dimensione della cache ha raggiunto il valore massimo.
10)ProgettoSII:l'applicazione main che sincronizza le chiamate principali dell'applicazione.
11)GenerateObjectURL:si occupa della rappresentazione dei dati di interesse per inserirli all'interno dell'indice attraverso la classe ObjectURL.

La relazione indexurl, cioè l'indice, memorizza i dati per effettuare la ricerca, dato un url, del segmento di interesse.
Questa relazione è composta dai seguenti attributi:

1)index:la chiave della relazione
2)url:url di interesse
3)segmentwarc:il nome del segmento .warc che memorizza l'url di cui sopra
4)actualcontentlenght:la dimensione totale del file .warc
5)offsetwarc:la posizione dell'url all'interno del file .warc

La relazione cache, cioè la rappresentazione della nostra cache, memorizza i dati per effettuare il retrival, dato un url, del contenuto della pagina di interesse.
Questa relazione è composta dai seguenti attributi:

1)segmentwarc:il nome del segmento .warc che memorizza l'url(è chiave della relazione)
2)filesize:la dimensione attuale, cioè attualmente scaricata, del file di cui sopra

PREREQUISITI PER IL FUNZIONAMENTO
1.Installazione del DBMS postgresql
2.Creazione della directory cache
3.All'interno della classe Configurations modificare il path del file di configurazione inserendo nella sua variabile d'istanza(pathFileConfiguarations) il path.
4.Impostare nel "file di configuarazione.txt":
il path della directory file warc.path dopo la stringa "folderFileWarcPath=". 
il path della directory file wat dopo la stringa "folderWat".
il path della directory file wat.path dopo la stringa "folderwatpath=".
il path della directory cache dopo la stringa "folderCache=".
il numero massimo di segmenti warc che possono essere prenti nella cache dopo la stringa "maxNumberWARCinCache=".
la grandezza massima in byte della cache dopo la stringa "maxSizeCache=".
user e password del Database

ANNOTAZIONI
Il file .wat e anche il file .warc possiedono, per ogni url, tre entry cioè una per la request HTTP, una per la response HTTP ed una per i metadata.
Abbiamo quindi utilizzato per la creazione dell'indice solamente la entry relativa alla response.
Nella relazione indexurl abbiamo inserito l'attributo actualcontentlength ma che in pratica non viene utilizzato, se si vuole si puo' togliere modificando le classi di cui sopra.
La variabile d'istanza relativa al massimo numero degli oggetti in cache deve essere ricontrollata perchè se ad esempio si impostano N=5 files massimi in cache e si sono chiesti 5 url differenti che si trovano in 5 differenti segmenti ma tutti in testa ai file(quindi ciascun file di piccolissime dimensioni) il piu' datato file viene eliminato, va quindi modificata la base di dati della cache mettendo un flag che specifica se il file è stato scaricato completamente o meno.
Standard è il numero massimo quindi modificare!!!