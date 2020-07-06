# Install

There are two scenarios how to setup the integration.

## Clean install in the current folder

The folderstructure of this project already contains everything that is needed to build and run SAP CX.

- get SAP CX install package, e.g. `CXCOMM190500P_12-70004140.ZIP`
- unzip `CXCOMM190500P_12-70004140.ZIP`

- install hybris using recipe `b2c_acc_plus`
	```
	cd installer
	./install.sh -r b2c_acc_plus -A initAdminPassword=nimda
	cd ..
	```

### Build
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

### Modify Accelerator templates

- go to template-modifier folder
	```
	cd template-modifier
	```
- run
	```
	./modify_templates.sh {your-folder-above-hybris}
	```

### Start server
- go to (target) platform
	```
	cd hybris/bin/platform
	```
- run
	```
	./hybrisserver.sh start
	```

## Install the extensions in an existing project

- copy the extensions `hybris/bin/custom/amplienceintegration` and `hybris/bin/custom/amplienceaddon` to your `hybris/bin/custom/` folder
- include those extensions in your `hybris/config/localextensions.xml` file
	```
  <!-- amplience integration -->
  <extension name='amplienceintegration' />
  <extension name='amplienceaddon' />
	```
- install the addon to your storefront, e.g.:
	```
	ant addoninstall -Daddonnames="amplienceaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
	```
- build your system, e.g.:
	```
	ant clean all
	```
- run an "Update Running System"

Keep in mind, that you have to use the taglib in your templates to show Amplience managed content. We do not modify any templates!
