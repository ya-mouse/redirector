SUBDIRS=AMI ATEN AVCT HUAWEI HP

all: $(SUBDIRS) redirector.jar

install: all
	install -pD -m644 redirector.jar $(DESTDIR)/usr/share/ipmitool6/redirector.jar

redirector.jar:
	rm -rf class; mkdir class
	for d in $(SUBDIRS); do rsync -a $$d/ class/; done
	cd class; find -name "*.class" -o -name "*.properties" -o -name "lib*.so" -o -name "LIB*.SO" -o -name "*.gif" | \
	    xargs jar cfe $(CURDIR)/$@ com/ipmitool6/AMI/kvm/Redirector

$(SUBDIRS):
	make -C $@ $(MAKECMDGOALS)

clean: $(SUBDIRS)
	rm -rf class redirector.jar

distclean: clean

.PHONY: all install clean distclean $(SUBDIRS) redirector.jar
