all: com/ipmitool6/HUAWEI/kvm/Redirector.class Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all

clean:
	rm -f com/ipmitool6/HUAWEI/kvm/Redirector.class

distclean: clean

.PHONY: all install clean distclean
