cd chase_logic
mvn clean install
cd ..
cd game
mvn clean install
cd target
java -jar game.jar --enemiesCount=3 --wallsCount=11 --size=8 --profile=production
