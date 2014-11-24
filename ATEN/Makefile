all: com/ipmitool6/ATEN/kvm/Redirector.class Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all
	mkdir -p $(DESTDIR)/usr/share/ipmitool6/ATEN
	install -p -m644 *.so $(DESTDIR)/usr/share/ipmitool6/ATEN

clean:
	rm -f com/ipmitool6/ATEN/kvm/Redirector.class \
	tw/com/aten/bean/InfoRepository.class \
	tw/com/aten/bean/VirtualUsbInfo.class \
	tw/com/aten/ikvm/ui/RemoteVideo.class \
	tw/com/aten/vstorage/VirtualStorage.class

distclean: clean

.PHONY: all install clean distclean
