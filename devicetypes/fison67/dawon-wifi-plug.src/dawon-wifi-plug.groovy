/**
 *  Dawon WiFi Plug (v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2018 fison67@nate.com
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
*/

 
import groovy.json.JsonSlurper

metadata {
	definition (name: "Dawon WiFi Plug", namespace: "fison67", author: "fison67") {
        capability "Actuator"
        capability "Switch"
        capability "Power Meter"
        capability "Energy Meter"
        capability "Refresh"
        
        attribute "lastCheckin", "Date"
        attribute "modelId", "string"
        attribute "feeType", "string"
        attribute "feeDate", "string"
        
        
	}

	simulator { }
    
    preferences {
        input name: "childLock", title:"Power Lock. It can't be power off." , type: "enum", required: true, defaultValue: "off", options: ["on", "off"]
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNTUg/MDAxNTIyNjcwODg1MTU2.KfRiLw6Uei1mX7djpXxo0jtKlsAWLOyz04yVtEU9yZsg.3A6PUr6aM1nn2mIaD4Rt7ws_bDZi9dKlzVJJLUoiLSAg.PNG.shin4299/plug_main_on.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTcy/MDAxNTIyNjcwODg0OTI5.Y6YSf8yKOH56h1RsLl0MbgFyHqqGw-E-XXQ6wG_g950g.vr4pyhi92iDk-u6pisNPGdGeTkJxaidmPe5y1rW-cAEg.PNG.shin4299/plug_main_off.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
                
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfMTcy/MDAxNTIyNjcwODg0OTI5.Y6YSf8yKOH56h1RsLl0MbgFyHqqGw-E-XXQ6wG_g950g.vr4pyhi92iDk-u6pisNPGdGeTkJxaidmPe5y1rW-cAEg.PNG.shin4299/plug_main_off.png?type=w3", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"https://postfiles.pstatic.net/MjAxODA0MDJfNTUg/MDAxNTIyNjcwODg1MTU2.KfRiLw6Uei1mX7djpXxo0jtKlsAWLOyz04yVtEU9yZsg.3A6PUr6aM1nn2mIaD4Rt7ws_bDZi9dKlzVJJLUoiLSAg.PNG.shin4299/plug_main_on.png?type=w3", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            
            tileAttribute("device.power", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Meter: ${currentValue} w\n ',icon: "st.Health & Wellness.health9")
            }
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'\nUpdated: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        valueTile("power", "device.power", width:2, height:2, inactiveLabel: false, decoration: "flat" ) {
        	state "power", label: '현재\n${currentValue} w', defaultState: true
		}    
        valueTile("energy", "device.energy", width:2, height:2, inactiveLabel: false, decoration: "flat" ) {
        	state "energy", label: '누적\n${currentValue}kWh', defaultState: true
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
        }
        valueTile("modelId", "device.modelId", width:2, height:1, inactiveLabel: false, decoration: "flat" ) {
        	state "modelId", label: '${currentValue}', defaultState: true
		}   
        valueTile("feeType", "device.feeType", width:4, height:1, inactiveLabel: false, decoration: "flat" ) {
        	state "feeType", label: '요금: ${currentValue}', defaultState: true
		} 
        valueTile("feeDate", "device.feeDate", width:2, height:1, inactiveLabel: false, decoration: "flat" ) {
        	state "feeDate", label: '정산일: ${currentValue}', defaultState: true
		} 
        
        
        main (["switch"])
        details(["switch", "power", "energy", "refresh", "modelId", "feeType", "feeDate"])
        
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setInfo(String app_url, String id) {
	log.debug "${app_url}, ${id}"
	state.app_url = app_url
    state.id = id
    
    refresh()
}

def setStatus(params){
    log.debug "${params.key} >> ${params.data}"
    def type = params.key
    def data = params.data
 
 	switch(type){
    case "power":
    	sendEvent(name:"switch", value: data)
    	break
    case "meter":
    	sendEvent(name:"power", value: data as double)
    	break
    case "meterH":
    	sendEvent(name:"energy", value: data as double)
    	break
    }
    
    updateLastTime()
}

def on(){
    def body = [
        "id": state.id,
        "cmd": "power",
        "data": "on"
    ]
    def options = makeCommand(body)
    sendCommand(options, null)
}

def off(){
    def childLockMode = childLock == null ? false : (childLock == "on" ? true : false)
    if(childLockMode){
    	log.warn "Child Lock Mode is On!!! This can't be power off."
    	sendEvent(name:"switch", value: "on", displayed: false)
    }else{
        def body = [
            "id": state.id,
            "cmd": "power",
            "data": "off"
        ]
        def options = makeCommand(body)
        sendCommand(options, null)
    }
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def refresh(){
	log.debug "Refresh"
    def options = [
     	"method": "GET",
        "path": "/devices/api/get/${state.id}",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ]
    ]
    sendCommand(options, callback)
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg
    try {
        msg = parseLanMessage(hubResponse.description)
		def jsonObj = new JsonSlurper().parseText(msg.body)
	//	log.debug jsonObj
        
        if(jsonObj.result){
        	def feeDate 		= jsonObj.data.info.device_profile.fee_date
            def feeType 		= jsonObj.data.info.device_profile.fee_stand
            def model 			= jsonObj.data.info.model_id
            
    		sendEvent(name: "modelId", value: model, displayed: false)
    		sendEvent(name: "feeDate", value: feeDate, displayed: false)
            
            
            def feeTypeList 	= jsonObj.data.fee
            
            for (def item : feeTypeList) {
                if(item.stand_code == feeType){
                	log.debug ">>" + item.display_name
    				sendEvent(name: "feeType", value: item.display_name, displayed: false)
                }
            }	
            
        }
	//	try{ sendEvent(name:"power", value: jsonObj.properties.powerLoad.value) }catch(err){}
        
        updateLastTime()
    } catch (e) {
        log.error "Exception caught while parsing data: "+e;
    }
}

def updated() {
}

def sendCommand(options, _callback){
	def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: _callback])
    sendHubCommand(myhubAction)
}

def makeCommand(body){
	def options = [
     	"method": "POST",
        "path": "/devices/api/control",
        "headers": [
        	"HOST": state.app_url,
            "Content-Type": "application/json"
        ],
        "body":body
    ]
    return options
}

