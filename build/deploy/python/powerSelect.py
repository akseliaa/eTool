import sys
import serial
import getopt

comPort = ''
pullup = ''
pullupState = ''
powerState = ''

ser = ''

def main(argv):
    try:
        opts, args = getopt.getopt(argv,"c:r:e:p:",["comPort=","pullup=","pullupState=","powerState="])
    except getopt.GetoptError:
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-c", "--comPort"):
            global comPort
            comPort = arg
        elif opt in ("-r", "--pullup"):
            global pullup
            pullup = arg
        elif opt in ("-e", "--pullupState"):
            global pullupState
            pullupState = arg
        elif opt in ("-p", "--powerState"):
            global powerState
            powerState = arg

    global ser
    ser=serial.Serial(comPort,115200,timeout = 1)
    assert ser.isOpen()

    setParameters()

    ser.close()

def send(cmd):
    ser.write(str(cmd+'\n').encode('ascii'))
    for line in ser.readlines():
        print(line.decode('utf-8').strip())

def setParameters():
    setPullupSource(pullup)
    setPullupState(pullupState)
    setPowerState(powerState)

def setPullupSource(source):
    send('e')
    if source == "ext":
        send('1')
    elif source == "3v3":
        send('2')
    elif source == "5v":
        send('3') 

def setPullupState(state):
    if state == "on":
        send('P')
    elif state == "off":
        send('p')

def setPowerState(state):
    if state == "on":
        send('W')
    elif state == "off":
        send('w')

if __name__ == "__main__":
    main(sys.argv[1:])