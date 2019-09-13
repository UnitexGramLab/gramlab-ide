cd ../unitex-core/build
make DEBUG=yes UNITEXTOOLLOGGERONLY=yes
cd ../../gramlab-ide
ant
cp ../unitex-core/bin/UnitexToolLogger dist/
cd dist
java -jar Unitex.jar
