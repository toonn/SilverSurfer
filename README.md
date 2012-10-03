SilverSurfer
============

Repository for code produced by team zilver for P&amp;O Computerwetenschappen

Een goede redelijk korte uitleg van git vind je hier: 
[Git reference](http://gitref.org/index.html)

Ik stel voor dat we aan de master branch niks veranderen, het is te zeggen dat
we enkel wanneer we iets moeten indienen veranderingen in de master branch
mergen. Verder stel ik voor dat iedereen die aan iets werk een branch maakt die
begint met zijn/haar naam en dan een korte benaming van hetgeen waaraan je gaat
werken in die branch e.g. toonn-GUI.

Ik weet nog niet of ik expliciet toegang moet verschaffen zodat iedereen aan
de repository kan. Dus als er vragen of problemen met access rights zijn laat
iets weten.

Hoe krijg ik het repository nu op mijn computer?
    (command line, cmd prompt is analoog)

Je opent een terminal of cmd prompt.

> cd /pad/naar/directory/waar/je/de/code/wil/hebben/staan  
> git clone [URL]

  Nu heb je normaal de code op je computer maar de actieve branch is
  waarschijnlijk nog master waar we zoveel mogelijk proberen afblijven.
  Dus maken we een nieuwe branch aan.

> cd ./SilverSurfer  
> git branch toonn-GUI (<- bvb.)  
> git checkout toonn-GUI

  Nu kan je zoveel aanpassen, committen, pushen... als je wil.
  [URL] Vind je bovenaan de github pagina van de repository.
  Het gemakkelijkst vind ik de ssh url maar dat moet ge zelf maar zien.
  Met 'git branch' kan je alle branches bekijken, naast de actieve staat een *.

Vragen moogt ge altijd stellen natuurlijk want dit is wellicht niet erg
duidelijk uitgeledgd.


Ons IRC channel voorlopig:
 "#silversurfer" op server "irc.nullirc.net"

