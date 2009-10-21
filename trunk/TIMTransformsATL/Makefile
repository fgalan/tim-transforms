VER=2009_10_15
PKG_NAME=TIMTransformsMDA-v$(VER)
DEST_DIR=/tmp/$(PKG_NAME)
TIMTRANSFORMS=../TIMTransforms

# This Makefile assumes the existence of an ../TIMTransforms directory (actually
# it is set using the $TIMTRANSFORMS variable) from which some pieces are taken
# to build the package. It also assumes Internet access to the DMTF web site

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
	mkdir $(DEST_DIR)/schema
	cd $(DEST_DIR)/schema && wget http://www.dmtf.org/standards/cim/cim_schema_v2220/cim_schema_2.22.0Final-MOFs.zip
	mkdir $(DEST_DIR)/scenarios
	cp -r $(TIMTRANSFORMS)/mof/basic*.mof $(DEST_DIR)/scenarios
	cp -r $(TIMTRANSFORMS)/mof/rediris*.mof $(DEST_DIR)/scenarios
	cp -r $(TIMTRANSFORMS)/mof/nsfnet*.mof $(DEST_DIR)/scenarios
	mkdir $(DEST_DIR)/tim
	cp -r $(TIMTRANSFORMS)/mof/tim.mof $(DEST_DIR)/tim
	cd /tmp && tar cfvz $(PKG_NAME).tar.gz $(PKG_NAME)
	
clean:
	rm -rf $(DEST_DIR)
	rm -rf /tmp/$(PKG_NAME).tar.gz
