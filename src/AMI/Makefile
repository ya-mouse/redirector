CLS = com/ipmitool6/AMI/kvm/Redirector.class \
	com/ami/iusb/CDROMRedir.class \
	com/ami/iusb/EN_StrTable.class \
	com/ami/iusb/FloppyRedir.class \
	com/ami/iusb/IUSBRedirSession.class

all: $(CLS) Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all
	mkdir -p $(DESTDIR)/usr/share/ipmitool6/AMI
	install -p -m644 *.so $(DESTDIR)/usr/share/ipmitool6/AMI

clean:
	rm -f $(CLS)

distclean: clean

.PHONY: all install clean distclean
