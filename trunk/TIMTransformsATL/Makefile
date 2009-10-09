VER=2009_10_15
PKG_NAME=TIMTransformsMDA-v$(VER)
DEST_DIR=/tmp/$(PKG_NAME)

pkg:
	mkdir $(DEST_DIR)
	cp -r inputs $(DEST_DIR)
	cp -r metamodels $(DEST_DIR)
	cp -r transformations $(DEST_DIR)
	cp README.txt $(DEST_DIR)
	cp build.xml $(DEST_DIR)
	rm -rf $(DEST_DIR)/metamodels/ecute
	rm -rf $(DEST_DIR)/transformations/TwoSteps
	rm -rf $(DEST_DIR)/transformations/XMLTransforms
	-find $(DEST_DIR) -name .svn -exec rm -rf {} \;
	cd /tmp && tar cfvz $(PKG_NAME).tar.gz $(PKG_NAME)
	
clean:
	rm -rf $(DEST_DIR)
	rm -rf /tmp/$(PKG_NAME).tar.gz
