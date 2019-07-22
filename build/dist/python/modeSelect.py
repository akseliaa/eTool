import sys
import getopt
import serial

comPort = ''
mode = ''
param1 = ''
param2 = ''
param3 = ''
param4 = ''
param5 = ''
param6 = ''
ser = ''

test = ""

def main(argv):
    try:
        opts, args = getopt.getopt(argv,"p:m:a:b:c:d:e:f:",["comPort=","mode=","param1=","param2=","param3=","param4=","param5=","param6="])
    except getopt.GetoptError:
        print('Error in class call')
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-p", "--comPort"):
            global comPort
            comPort = arg
        elif opt in ("-m", "--mode"):
            global mode
            mode = arg
        elif opt in ("-a", "--param1"):
            global param1
            param1 = arg
        elif opt in ("-b", "--param2"):
            global param2
            param2 = arg
        elif opt in ("-c", "--param3"):
            global param3
            param3 = arg
        elif opt in ("-d", "--param4"):
            global param4
            param4 = arg
        elif opt in ("-e", "--param5"):
            global param5
            param5 = arg
        elif opt in ("-f", "--param6"):
            global param6
            param6 = arg

    global ser
    ser=serial.Serial(comPort, 115200, timeout=1) 
    assert ser.isOpen()

    func = getMode(mode)
    func()

    ser.close()

def send(cmd):
    ser.write(str(cmd+'\n').encode('ascii'))
    for line in ser.readlines():
        print(line.decode('utf-8').strip())

def getMode(wantedMode):
    def hiz():
        print('hiz')
        send('#')
        send('m')
        send('1')
    def onewire():
        send('#')
        send('m')
        send('2')
    def uart():
        send('#')
        send('m')
        send('3')
        send(param1)
        send(param2)
        send(param3)
        send(param4)
        send(param5)
    def i2c():
        send('#')
        send('m')
        send('4')
        send(param1)
        send(param2)
    def spi():
        send('#')
        send('m')
        send('5')
        send(param1)
        send(param2)
        send(param3)
        send(param4)
        send(param5)
        send(param6)
    def twowire():
        send('#')
        send('m')
        send('6')
        send(param1)
        send(param2)
    def threewire():
        send('#')
        send('m')
        send('7')
        send(param1)
        send(param2)
        send(param3)
    def keyb():
        send('#')
        send('m')
        send('8')
    def lcd():
        send('#')
        send('m')
        send('9')
    def pic():
        send('#')
        send('m')
        send('10')
        send(param1)
        send(param2)

    return {
        'hiz': hiz,
        'onewire': onewire,
        'uart': uart,
        'i2c': i2c,
        'spi': spi,
        'twowire': twowire,
        'threewire': threewire,
        'keyb': keyb,
        'lcd': lcd,
        'pic': pic
    }[wantedMode]

if __name__ == "__main__":
    main(sys.argv[1:])