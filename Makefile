all: redir

redir:
	make -C src $(MAKECMDGOALS)

install: all redir
	for bin in redirector; do \
	    install -pD -m755 $$bin $(DESTDIR)/usr/bin/$$bin; \
	done
	for redir in redirector-AMI redirector-ATEN redirector-HUAWEI redirector-HP redirector-AVCT; do \
	    install -pD -m644 $$redir $(DESTDIR)/usr/share/redirector/$$redir; \
	done

distclean clean: redir

.PHONY: all redir install clean distclean
