VER=2009_02_21
PKG_NAME=TIMTransforms-v$(VER)
DEST_DIR=/tmp/$(PKG_NAME)

pkg:
	mkdir $(DEST_DIR)
	cp -r src $(DEST_DIR)
	cp -r doc $(DEST_DIR)
	cp -r mof $(DEST_DIR)
	cp -r scripts $(DEST_DIR)
	mkdir $(DEST_DIR)/lib
	cp /data/fermin/Ph.D/validation/PFC-David_Davila/ClientePBNMv1.1/src/ClientePBNM_v1.1/wbem.jar $(DEST_DIR)/lib
	cp README.txt $(DEST_DIR)
	cd /tmp && tar cfvz $(PKG_NAME).tar.gz $(PKG_NAME)
	
clean:
	rm -rf $(DEST_DIR)
	rm -rf /tmp/$(PKG_NAME).tar.gz
