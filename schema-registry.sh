#!/bin/bash

if [ "$1" = "create" ]
then
#	cat src/main/resources/payment-schema.json
    #curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
#	    --data "$(cat src/main/resources/payment-schema.json)" \
#	    http://localhost:8081/subjects/payments-value/versions
	curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
		 --data-binary "@$(pwd)/src/main/resources/payment-schema.json" \
		 http://localhost:8081/subjects/payments-value/versions
#	curl  -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
#       	--data '{"schema": "{\"type\":\"record\",\"name\":\"Payment\",\"namespace\":\"io.onurb.examples.kafka.security\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"amount\",\"type\":\"double\"},{\"name\":\"iban\",\"type\":\"string\"}]}"}' \
#		http://localhost:8081/subjects/payments-value/versions
fi

#curl -X DELETE http://localhost:8081/subjects/payments-value/versions/2

curl http://localhost:8081/subjects/payments-value/versions/latest
