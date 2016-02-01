.PHONY: all requirements doc install reinstall clean serve-dev serve-prod

APP = fdp
HOST_DEV = 127.0.0.1:8080
HOST_PROD = fdp.biotools.nl:8080

all: install

requirements:
	pip install -r requirements.txt

doc:
	git clone https://github.com/swagger-api/swagger-ui.git
	mv swagger-ui/dist/* doc
	rm -fr swagger-ui
	sed -i .org 's/http:.*\(swagger.json\)/\/doc\/\1/' doc/index.html

install: requirements doc

reinstall:
	pip install --upgrade --no-deps .

clean:
	rm -f *.pyc
	find doc ! -name swagger.json -delete

serve-dev:
	python -m bottle --debug --reload --bind $(HOST_DEV) $(APP)

serve-prod:
	nohup python -m bottle -b $(HOST_PROD) $(APP) &

