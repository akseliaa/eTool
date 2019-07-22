import sys
import serial
import getopt
import xml.etree.cElementTree as et

comPort = ''
ser = ''

def main(argv):
    try:
        opts, args = getopt.getopt(argv,"c:",["comPort="])
    except getopt.GetoptError:
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-c", "--comPort"):
            comPort = arg

    global ser
    ser=serial.Serial(comPort, 115200, timeout=1)
    assert ser.isOpen()

    readAddress()

    ser.close()

def sendAndRead(cmd):
    list = []
    ser.write(str(cmd+'\n').encode('ascii'))
    for line in ser.readlines():
        decoded = line.decode('utf-8').strip()
        if line.strip():
            list.append(decoded)
    return list

def readAddress():
    list = sendAndRead('(1)')
    list.remove(list[0])
    list.remove(list[0])
    list.remove(list[len(list)-1])
    print(list)
    for item in list:
        print(item)
    createXmlDocument(list)

def createXmlDocument(list):
    root = et.Element("root")
    for line in list:
        address = et.SubElement(root,"address")
        addressList = line.split(')',1)
        writeAddress = ''
        readAddress = ''
        for part in addressList:
            separated = part.split('(')
            if 'W' in separated[1]:
                writeAddress = separated[0].strip()
            elif 'R' in separated[1]:
                readAddress = separated[0].strip()
        write = et.SubElement(address, "write").text = writeAddress
        read = et.SubElement(address, "read").text = readAddress
        print(writeAddress)
        print(readAddress)

    tree = et.ElementTree(root)
    tree.write("address.xml")

if __name__ == "__main__":
    main(sys.argv[1:])