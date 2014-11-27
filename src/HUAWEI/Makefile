CLS = com/redirector/HUAWEI/kvm/Redirector.class

all: $(CLS) Makefile

%.class: %.java
	javac -O $^ -target 1.5 -source 1.5

install: all

clean:
	rm -f $(CLS)

distclean: clean

.PHONY: all install clean distclean
