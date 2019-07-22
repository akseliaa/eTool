import sys
import serial
import getopt
import intelhex

comPort = ''
ser = ''
filePath = ''
writeAddress = ''
readAddress = ''
highAddress = ''
lowAddress = ''
length = ''


def main(argv):
    try:
        opts, args = getopt.getopt(argv,"c:f:w:r:h:l:s:",["comport=","filePath=","writeAddress=","readAddress=","highAddress=","lowAddress=","length="])
    except getopt.GetoptError:
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-c", "--comPort"):
            global comPort
            comPort = arg
        elif opt in ("-r", "--readAddress"):
            global readAddress
            readAddress = arg
        elif opt in ("-w", "--writeAddress"):
            global writeAddress
            writeAddress = arg
        elif opt in ("-f", "--filePath"):
            global filePath
            filePath = arg
        elif opt in ("-h", "--highAddress"):
            global highAddress
            highAddress = arg
        elif opt in ("-l", "--lowAddress"):
            global lowAddress
            lowAddress = arg
        elif opt in ("-s", "--length"):
            global length
            length = arg

    global ser
    ser=serial.Serial(comPort, 115200, timeout = 1)
    assert ser.isOpen()

    setAddress(writeAddress, highAddress, lowAddress)
    readData(readAddress, length)

    ser.close()

def send(cmd):
    ser.write(str(cmd+'\n').encode('ascii'))
    for line in ser.readlines():
        print(line.decode('utf-8').strip())

def sendAndRead(cmd):
    list = []
    ser.write(str(cmd+'\n').encode('ascii'))
    for line in ser.readlines():
        decoded = line.decode('utf-8').strip()
        if decoded:
            list.append(decoded)
    return list

def setAddress(writeAddress, highAddress, lowAddress):
    command = "[{} {} {}]".format(writeAddress,highAddress, lowAddress)
    send(command)

def readData(readAddress, length):
    array = bytearray()
    command = "[{} r:{}]".format(readAddress, length)
    data = sendAndRead(command)
    file = open(filePath,"wb+")
    for line in data:
        if "READ: " in line:
            dataBytes = line.split(' ')
            for byte in dataBytes:
                if "0x" in byte:
                    array.append(int(byte,16))
    file.write(array)
    file.close()

    ih = intelhex.IntelHex()
    ih.fromfile(filePath, format='bin')
    
    newPath = filePath.replace('.hex','(i).hex')
    tiedosto = open(newPath,"w+")
    ih.write_hex_file(tiedosto)
    tiedosto.close
    print("working")


if __name__  == "__main__":
    main(sys.argv[1:])
