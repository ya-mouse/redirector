CLS = com/redirector/HP/kvm/Redirector.class \
	com/serverengines/mahogany/CConn.class \
	com/serverengines/mahogany/MessageReceiverThread.class \
	com/serverengines/mahogany/MahoganyViewer.class \
	com/serverengines/mahogany/MasterThread.class

all: $(CLS) Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all M2.JAR
	install -pD -m644 M2.JAR $(DESTDIR)/usr/share/redirector/HP/M2.JAR
	install -p -m644 *.SO $(DESTDIR)/usr/share/redirector/HP

clean:
	rm -f $(CLS)

distclean: clean

.PHONY: all install clean distclean
