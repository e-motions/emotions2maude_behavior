package main.java.transformation;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Maude.MaudePackage;
import behavior.BehaviorPackage;
import gcs.GcsPackage;

public class ModelManager {
	
	private Resource behModel, maudeModel;
	private ResourceSet resourceSet;
	
	public ModelManager(File behaviorModel, File gcsModel, File maudeModel) {
		
		/* initialize resource set and packages */
		resourceSet = this.initEMF();
		
		/* Create URIs */
		URI _behURI = URI.createFileURI(behaviorModel.getAbsolutePath());
		URI _gcsURI = URI.createFileURI(gcsModel.getAbsolutePath());
		URI _maudeURI = URI.createFileURI(maudeModel.getAbsolutePath());
		
		/* Load resources */
		Resource behResource = resourceSet.createResource(_behURI);
		Resource gcsResource = resourceSet.createResource(_gcsURI);
		Resource maudeResource = resourceSet.createResource(_maudeURI);
		
		try {
			behResource.load(null);
			gcsResource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EcoreUtil.resolveAll(resourceSet);
		
	    behModel = behResource;
	    this.maudeModel = maudeResource;
	}
	
	private ResourceSet initEMF(){
		Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
	    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
	    _extensionToFactoryMap.put("*", _xMIResourceFactoryImpl);
	    
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		
		EPackage.Registry _packageRegistry = resourceSet.getPackageRegistry();
		
		/* Loading required packages */
		_packageRegistry.put(MaudePackage.eNS_URI, MaudePackage.eINSTANCE);
		_packageRegistry.put(BehaviorPackage.eNS_URI, BehaviorPackage.eINSTANCE);
		_packageRegistry.put(GcsPackage.eNS_URI, GcsPackage.eINSTANCE);
		
		return resourceSet;
	}
	
	public Resource getBehModel() {
		return behModel;
	}
	
	public Resource getMaudeModel() {
		return maudeModel;
	}
}
