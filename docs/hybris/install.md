# Install

- get `CXCOMM190500P_12-70004140.ZIP`
- unzip `CXCOMM190500P_12-70004140.ZIP`

- install hybris using recipe `b2c_acc_plus`
	```
	cd installer
	./install.sh -r b2c_acc_plus -A initAdminPassword=nimda
	cd ..
	```

# Build
- go to platform
	```
	cd hybris/bin/platform
	```
- run 
	```
	. ./setantenv.sh
	ant clean all
	ant addoninstall -Daddonnames="amplienceaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
	ant clean all
	```

# Start server
- go to (target) platform
	```
	cd hybris/bin/platform
	```
- run
	```
	./hybrisserver.sh start
	```
