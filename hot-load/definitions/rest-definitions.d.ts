declare module "rest-definitions" {
    /**
     * Annotations for properties on 'inout' interfaces:
     * - generated: This property cannot be specified on any input requests (PUT/PATCH/POST).
     *              As a result, generated properties are always marked as optional.
     * - key: This property is the identifier for an object, with certain uniqueness constraints.
     */

    /*inout*/
    export interface AccessKey {
        createdBy: string;
        /*generated*/ createdTime?: number;
        description?: string;
        /*generated key*/ name?: string;
    }

    /*out*/
    export interface DeploymentMetrics {
        [packageLabelOrAppVersion: string]: UpdateMetrics
    }

    /*in*/
    export interface DeploymentStatusReport {
        appVersion: string;
        clientUniqueId?: string;
        deploymentKey: string;
        previousDeploymentKey?: string;
        previousLabelOrAppVersion?: string;
        label?: string;
        status?: string;
    }

    /*in*/
    export interface DownloadReport {
        clientUniqueId: string;
        deploymentKey: string;
        label: string;
    }

    /*inout*/
    export interface PackageInfo {
        appVersion?: string;
        description?: string;
        isDisabled?: boolean;
        isMandatory?: boolean;
        /*generated*/ label?: string;
        /*generated*/ packageHash?: string;
        rollout?: number;
    }

    /*out*/
    export interface UpdateCheckResponse extends PackageInfo {
        downloadURL?: string;
        isAvailable: boolean;
        packageSize?: number;
        shouldRunBinaryVersion?: boolean;
        updateAppVersion?: boolean;
    }

    /*out*/
    export interface UpdateCheckCacheResponse {
        originalPackage: UpdateCheckResponse;
        rollout?: number;
        rolloutPackage?: UpdateCheckResponse;
    }

    /*in*/
    export interface UpdateCheckRequest {
        appVersion: string;
        clientUniqueId?: string;
        deploymentKey: string;
        isCompanion?: boolean;
        label?: string;
        packageHash?: string;
    }

    /*out*/
    export interface UpdateMetrics {
        active: number;
        downloaded?: number;
        failed?: number;
        installed?: number;
    }

    /*inout*/
    export interface Account {
        /*key*/ email: string;
        name: string;
    }

    /*out*/
    export interface CollaboratorProperties {
        isCurrentAccount?: boolean;
        permission: string;
    }

    /*out*/
    export interface CollaboratorMap {
        [email: string]: CollaboratorProperties;
    }

    /*inout*/
    export interface App {
        /*generated*/ collaborators?: CollaboratorMap;
        /*key*/ name: string;
    }

    /*in*/
    export interface AppCreationRequest extends App {
        manuallyProvisionDeployments?: boolean;
    }

    /*inout*/
    export interface Deployment {
        /*generated key*/ key?: string;
        /*key*/ name: string;
        /*generated*/ package?: Package
    }

    /*inout*/
    export interface Package extends PackageInfo {
        /*generated*/ blobUrl: string;
        /*generated*/ diffAgainstPackageHash?: string;
        /*generated*/ diffBlobUrl?: string;
        /*generated*/ diffSize?: number;
        /*generated*/ originalLabel?: string;       // Set on "Promote" and "Rollback"
        /*generated*/ originalDeployment?: string;  // Set on "Promote"
        /*generated*/ releasedBy?: string;          // Set by commitPackage
        /*generated*/ releaseMethod?: string;       // "Upload", "Promote" or "Rollback". Unknown if unspecified
        /*generated*/ size: number;
        /*generated*/ uploadTime: number;
    }
}