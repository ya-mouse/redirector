CLS = com/redirector/AVCT/kvm/Redirector.class \
	com/avocent/vm/abstractJFrameVM.class \
	com/avocent/vm/ApplianceSession.class \
	com/avocent/vm/ApplianceSession$$_cls4.class \
	com/avocent/vm/ApplianceSession$$DSPacketIO.class \
	com/avocent/vm/ApplianceSession$$FileStuff.class \
	com/avocent/vm/ApplianceSession$$ThreadDSDataInput.class \
	com/avocent/vm/ApplianceSession$$ThreadDSDataOutput.class \
	com/avocent/vm/AX509TrustManager.class \
	com/avocent/vm/AX509TrustManager$$_cls1.class \
	com/avocent/vm/JFrameVirtualMedia.class \
	com/avocent/vm/LocalDrives.class \
	com/avocent/vm/SessionParameters.class \
	com/avocent/vm/VirtualMedia.class \
	com/avocent/vm/VirtualMedia$$LoginStatus.class

all: $(CLS) Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all
	mkdir -p $(DESTDIR)/usr/share/redirector/AVCT
	install -p -m644 *.so $(DESTDIR)/usr/share/redirector/AVCT

clean:
	rm -f $(CLS)

distclean: clean

.PHONY: all install clean distclean
