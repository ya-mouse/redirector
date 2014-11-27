all: com/ipmitool6/HP/kvm/Redirector.class com/serverengines/mahogany/CConn.class com/serverengines/mahogany/MessageReceiverThread.class Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all M2.JAR
	install -pD -m644 M2.JAR $(DESTDIR)/usr/share/ipmitool6/HP/M2.JAR
	install -p -m644 *.SO $(DESTDIR)/usr/share/ipmitool6/HP

clean:
	rm -f com/ipmitool6/HP/kvm/Redirector.class com/serverengines/mahogany/CConn.class com/serverengines/mahogany/MessageReceiverThread.class \
	com/serverengines/mahogany/MahoganyViewer.class com/serverengines/mahogany/MasterThread.class


distclean: clean

.PHONY: all install clean distclean
