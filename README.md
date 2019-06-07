# PDFAnonymous
Extraction et Anonymisation de fichier PDF

usage: PDFAnonymous [-help] [-of <arg>] [-pdf <arg>] [-strict] [-tf <arg>]
       [-ts <arg>]
  
 -help        Aide sur la ligne de commande
 
 -of <arg>    Chemin complet du fichier de sortie au format ASCII UTF-8...
  
 -pdf <arg>   Chemin complet du fichier PDF en entree...
  
 -strict      Respect stricte de la casse des tokens a remplacer.
 
 -tf <arg>    Chemin complet du fichier des tokens de remplacement (voir
              format attendu)...
  
 -ts <arg>    Utilisation d'une chaine de tokens pour le remplacement  :
              au format "t1:t2#t3:t4#tn:tm", cette chaine doit etre
              delimite par des "" et est prioritaire sur la prise en
              charge du fichier de tokens...
  
Tondeur Herve (2019) sous Licence GPL V3.


Aide detaillee
--------------
L'option -pdf <PdfInputFile> est obligatoire 

L'option -of <ExtractDestFile> peut etre omise, dans ce cas l'extraction se fait directement sur la console.

L'option -ts permet de définir une liste de tokens directement sur la ligne de commande
le format de cette chaine de tokens doit respecter le syntaxe suivante :
TokenARemplacer:TokenDeRemplacement (separateur : obligatoire)
chaque couple de tokens doit etre separe par un caractere #
la liste des tokens doit etre entoure des caracteres double quotes "
Exemple : "2019:AAAA#Hello:Greating#today:tomorrow"
NB: Si l'option -ts n'est pas presente, il est obligatoire d'utiliser un fichier de tokens avec l'option -tf

L'option -tf <nomDuFichier>
Ce fichier doit comporter la liste des tokens (un couple par ligne) separe par un retour chariot
exemple de contenu : 
2019:AAAA
Hello:Greating
today:tomorrow
Le fichier doit etre enregistre au format ASCII UTF-8, aucune extension imposee

L'option -strict est optionnelle, elle permet de forcer la correspondance stricte sur le token a remplacer
si l'option n'est pas presente, l'application ne prend pas en compte la casse et remplace tous les tokens correspondant

