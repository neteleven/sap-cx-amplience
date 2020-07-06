<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="amp" uri="http://neteleven.de/tld/ampliencetags"%>

<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="Section1" var="comp" element="div" class="productDetailsPageSection1">
		<cms:component component="${comp}" element="div" class="productDetailsPageSection1-component"/>
	</cms:pageSlot>
	<product:productDetailsPanel />

	<!-- amplience-integration-start-->
	<amp:ampSlot position="stage-slot" context="product" lookup="${product.code}"/>
	<!-- amplience-integration-end-->

	<cms:pageSlot position="CrossSelling" var="comp" element="div" class="productDetailsPageSectionCrossSelling">
		<cms:component component="${comp}" element="div" class="productDetailsPageSectionCrossSelling-component"/>
	</cms:pageSlot>
	<cms:pageSlot position="Section2" var="comp" element="div" class="productDetailsPageSection2">
		<cms:component component="${comp}" element="div" class="productDetailsPageSection2-component"/>
	</cms:pageSlot>
	<cms:pageSlot position="Section3" var="comp" element="div" class="productDetailsPageSection3">
		<cms:component component="${comp}" element="div" class="productDetailsPageSection3-component"/>
	</cms:pageSlot>
	<cms:pageSlot position="UpSelling" var="comp" element="div" class="productDetailsPageSectionUpSelling">
		<cms:component component="${comp}" element="div" class="productDetailsPageSectionUpSelling-component"/>
	</cms:pageSlot>
	<product:productPageTabs />

	<!-- amplience-integration-start-->
	<amp:ampSlot position="main-content-slot" context="product" lookup="${product.code}"/>
	<!-- amplience-integration-end-->

	<cms:pageSlot position="Section4" var="comp" element="div" class="productDetailsPageSection4">
		<cms:component component="${comp}" element="div" class="productDetailsPageSection4-component"/>
	</cms:pageSlot>
</template:page>
