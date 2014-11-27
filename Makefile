all: redirector

redirector:
	make -C src $(MAKECMDGOALS)

install: all redirector
	for bin in ipmitool6; do \
	    install -pD -m755 $$bin $(DESTDIR)/usr/bin/$$bin; \
	done
	for ipmi6 in ipmitool6-AMI ipmitool6-ATEN ipmitool6-HUAWEI ipmitool6-HP ipmitool6-AVCT; do \
	    install -pD -m644 $$ipmi6 $(DESTDIR)/usr/share/ipmitool6/$$ipmi6; \
	done

distclean clean: redirector

.PHONY: all install clean distclean
