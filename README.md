# DW-Connector

Connector for Dawon WiFi Devices with SmartThings


# Install
#### Preparing
```
You need a Raspbery pi or Synology Nas to install DW Connector API Server(Default port: 30040)
```
<br/><br/>

## Install API Server<br/>
#### Raspberry pi<br/>
> You must install docker first.
```
sudo mkdir /docker
sudo mkdir /docker/dw-connector
sudo chown -R pi:pi /docker
docker pull fison67/dw-connector-rasp:0.0.1
docker run -d --restart=always -v /docker/dw-connector:/config --name=dw-connector-rasp --net=host fison67/dw-connector-rasp:0.0.1
```

###### Synology nas<br/>
> You must install docker first.<br/>
```
make folder /docker/dw-connector
Run Docker
-> Registery 
-> Search fison67/dw-connector
-> Advanced Settings
-> Volume tab -> folder -> Select dw-connector & Mount path '/config'
-> Network tab -> Check 'use same network as Docker Host'
-> Complete
```


## Install DTH<br/>

<br/><br/>

## Install Smartapps<br/>

