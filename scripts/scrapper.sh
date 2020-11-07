#!/bin/bash

LOCATIONS=("inmu1" "usmi" "usny" "uswd" "usla" "usnj1" "usmi2" "ussf" "usch" "usda" "usla3" "usla2" "usnj3" "usse" "usde" "ussl1" "usta1" "usla1" "usny2" "usho" "usda2" "usat" "usla5" "ussm" "sgju" "sgcb" "sgmb" "br" "br2" "hk2" "hk3" "hk4" "cato" "cava" "cato2" "camo" "ukdo" "ukel" "uklo" "ukwe" "defr1" "denu" "defr2" "defr3" "jpto" "jpyo" "jpto2" "nlth" "nlam" "nlro" "nlam2" "aume" "ausy" "aupe" "aubr" "ausy2" "frpa1" "frst" "frpa2" "ch2" "ch" "kr2" "ph" "my" "lk" "pk" "kz" "th" "id" "tw3" "vn" "mo" "kh" "mn" "la" "mm" "np" "kg" "uz" "bd" "bt" "bnbr" "mx" "pa" "cl" "ar" "cr" "co" "ve" "ec" "gt" "pe" "uy" "bs" "se" "itmi" "itco" "ro" "im" "esma" "esba" "tr" "ie" "is" "no" "dk" "be" "fi" "gr" "pt" "at" "am" "pl" "lt" "lv" "ee" "cz" "ad" "me" "ba" "lu" "hu" "bg" "by" "ua" "mt" "li" "cy" "al" "hr" "si" "sk" "mc" "je" "mk" "md" "rs" "ge" "az" "za" "il" "eg" "ke" "dz")

PAGES=1
FILE="/home/edu/ML/scrapper.txt"
FILE_BANNED="/home/edu/ML/scrapper-banned.txt"
SHOW_BROWSER=true

while [ $PAGES -lt 100 ]; do

	expressvpn disconnect
	expressvpn refresh
	expressvpn connect ${LOCATIONS[${PAGES}]}
	echo ${PAGES} ${LOCATIONS[${PAGES}]}

	java -jar /home/edu/ML/scrapper.jar ${FILE} ${FILE_BANNED} "${PAGES}" ${SHOW_BROWSER}
	
PAGES=$[$PAGES+1]
done




