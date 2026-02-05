import { useState, useEffect, useCallback, useRef } from "react";
import { ProductGrid } from "@/components/inventory/product-grid";
import { ProductDetail } from "@/components/inventory/product-detail";
import { InventoryFilters } from "@/components/inventory/inventory-filters";
import { InventoryRegisterModal } from "@/components/inventory/inventory-register-modal";
import type { Product, ProductCategory, ProductStatus, SortOption } from "@/types/inventory";
import {
  getInventoryItems,
  mapApiItemToProduct,
  mapUiStatusToApiStatus,
  mapApiStatusToUiStatus,
  mapUiSortToApiSort,
  addInventoryItem,
  deleteInventoryItem,
  getInventoryItemDetail,
  updateInventoryItemStatus,
  getStatusUpdateErrorMessage,
  toggleInventoryItemFavorite,
  recognizeProductByImage,
} from "@/api/inventoryApi";
import type { ItemStatus } from "@/types/inventory";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import { AlertModal } from "@/components/common/alert-modal";
import { ConfirmModal } from "@/components/common/confirm-modal";
import type { AlertModalType } from "@/components/common/alert-modal";
import type { ProductListItem } from "@/types/product";
type PhotoStep = "camera" | "preview" | "loading" | "result";

export default function InventoryPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(false);
  const [hasFetched, setHasFetched] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<ProductCategory | "all">("all");
  const [sortOption, setSortOption] = useState<SortOption>("recent");
  const [selectedStatus, setSelectedStatus] = useState<ProductStatus | "all">("all");
  const [favoriteFilter, setFavoriteFilter] = useState<boolean>(false);
  const [registerModalOpen, setRegisterModalOpen] = useState(false);
  const [photoModalOpen, setPhotoModalOpen] = useState(false);
  const [cameraError, setCameraError] = useState<string | null>(null);
  const [cameraLoading, setCameraLoading] = useState(false);
  const [photoStep, setPhotoStep] = useState<PhotoStep>("camera");
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [recognizedProduct, setRecognizedProduct] = useState<ProductListItem | null>(null);
  const [photoError, setPhotoError] = useState<string | null>(null);
  const [registering, setRegistering] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [productToDelete, setProductToDelete] = useState<Product | null>(null);
  const { toast } = useToast();
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const mediaStreamRef = useRef<MediaStream | null>(null);
  const confirmResolverRef = useRef<((value: boolean) => void) | null>(null);
  const [alertState, setAlertState] = useState({
    open: false,
    title: "알림",
    message: "",
    type: "info" as AlertModalType,
    confirmText: "확인",
  });
  const [confirmState, setConfirmState] = useState({
    open: false,
    title: "확인",
    message: "",
    type: "warning" as AlertModalType,
    confirmText: "확인",
    cancelText: "취소",
  });

  const showAlert = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string }) => {
    setAlertState({
      open: true,
      title: options.title ?? "알림",
      message: options.message,
      type: options.type ?? "info",
      confirmText: options.confirmText ?? "확인",
    });
  };

  const requestConfirm = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string; cancelText?: string }) =>
    new Promise<boolean>((resolve) => {
      confirmResolverRef.current = resolve;
      setConfirmState({
        open: true,
        title: options.title ?? "확인",
        message: options.message,
        type: options.type ?? "warning",
        confirmText: options.confirmText ?? "확인",
        cancelText: options.cancelText ?? "취소",
      });
    });

  const handleConfirmResult = (result: boolean) => {
    confirmResolverRef.current?.(result);
    confirmResolverRef.current = null;
    setConfirmState((prev) => ({ ...prev, open: false }));
  };

  // API 데이터 가져오기
  const fetchInventory = useCallback(async () => {
    setLoading(true);
    try {
      const filters: any = {
        page: currentPage,
        size: 20,
      };

      // 카테고리 필터 적용
      if (selectedCategory !== "all") {
        filters.category = selectedCategory.toUpperCase();
      }

      // 상태 필터 적용
      if (selectedStatus !== "all") {
        filters.status = mapUiStatusToApiStatus(selectedStatus);
      }

      // 즐겨찾기 필터 적용
      if (favoriteFilter) {
        filters.isFavorite = true;
      }

      // 정렬 옵션 적용
      filters.sort = mapUiSortToApiSort(sortOption);

      const response = await getInventoryItems(filters);

      const mappedProducts = response.content.map(mapApiItemToProduct);
      setProducts(mappedProducts);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error("Failed to fetch inventory:", error);
      setProducts([]);
      setSelectedProduct(null);
    } finally {
      setLoading(false);
      setHasFetched(true);
    }
  }, [currentPage, selectedCategory, selectedStatus, favoriteFilter, sortOption]);

  // 초기 로드 및 필터/정렬 변경 시 데이터 재조회
  useEffect(() => {
    fetchInventory();
  }, [fetchInventory]);

  // 필터가 변경되면 첫 페이지로 이동
  useEffect(() => {
    setCurrentPage(0);
  }, [selectedCategory, selectedStatus, favoriteFilter, sortOption]);

  // 검색 필터 (클라이언트 사이드)
  const filteredProducts = products.filter((product) => {
    if (searchQuery && !product.name.toLowerCase().includes(searchQuery.toLowerCase())) {
      return false;
    }
    return true;
  });
  const shouldShowPagination = totalPages > 1;
  const pageNumbers = Array.from({ length: totalPages }, (_, index) => index);

  const mapDetailCategoryToUI = useCallback((categoryMedium?: string): ProductCategory => {
    const categoryMap: Record<string, ProductCategory> = {
      스킨케어: "skin",
      메이크업: "beauty",
      헤어케어: "hair",
    };

    return categoryMedium ? categoryMap[categoryMedium] || "skin" : "skin";
  }, []);

  const handleProductSelect = useCallback(
    async (product: Product) => {
      setSelectedProduct(product);
      setDetailLoading(true);

      try {
        const response = await getInventoryItemDetail(product.id);
        const data = response.data;
        const categoryMedium = data.categoryMedium ?? data.productInfoDto.categoryMedium;

        // 만료일까지 남은 일수 계산
        let daysUntilExpiry: number | undefined = undefined;
        if (data.expectedExpiry) {
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          const expiryDate = new Date(data.expectedExpiry);
          expiryDate.setHours(0, 0, 0, 0);
          const diffTime = expiryDate.getTime() - today.getTime();
          daysUntilExpiry = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        }

        const normalizedStatus = data.status === "IN_USE" ? "OWNED" : data.status;
        const detailedProduct: Product = {
          id: data.id.toString(),
          name: data.productInfoDto.name,
          brand: data.productInfoDto.brandName ?? product.brand,
          category: mapDetailCategoryToUI(categoryMedium),
          image: data.productInfoDto.imageUrl,
          purchaseDate: data.purchaseDate,
          expirationDate: data.expectedExpiry,
          repurchaseCount: data.purchaseCount,
          status: mapApiStatusToUiStatus(normalizedStatus as ItemStatus),
          purchaseLink: data.productInfoDto.productUrl,
          isFavorite: data.isFavorite,
          daysUntilExpiry: daysUntilExpiry,
        };

        setSelectedProduct(detailedProduct);
      } catch (error) {
        console.error("Failed to fetch item detail:", error);
        toast({
          title: "상세 정보 로드 실패",
          description: "기본 정보만 표시됩니다.",
          variant: "destructive",
        });
      } finally {
        setDetailLoading(false);
      }
    },
    [mapDetailCategoryToUI, toast]
  );

  useEffect(() => {
    if (!hasFetched || loading) return;

    if (products.length === 0) {
      if (selectedProduct !== null) {
        setSelectedProduct(null);
      }
      return;
    }

    if (!selectedProduct || !products.some((product) => product.id === selectedProduct.id)) {
      handleProductSelect(products[0]);
    }
  }, [products, selectedProduct, hasFetched, loading, handleProductSelect]);

  const handleToggleFavorite = async (productId: string) => {
    // 중복 요청 방지
    if (favoriteLoading) return;

    setFavoriteLoading(true);
    try {
      // API 호출하여 즐겨찾기 토글
      const result = await toggleInventoryItemFavorite(productId);

      // 서버 응답의 isFavorite 값으로 UI 업데이트
      setProducts((prev) => prev.map((p) => (p.id === productId ? { ...p, isFavorite: result.isFavorite } : p)));

      if (selectedProduct?.id === productId) {
        setSelectedProduct((prev) => (prev ? { ...prev, isFavorite: result.isFavorite } : null));
      }
    } catch (error) {
      console.error("즐겨찾기 토글 실패:", error);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const handleDelete = (productId: string) => {
    const product = products.find((p) => p.id === productId);
    if (product) {
      setProductToDelete(product);
      setDeleteDialogOpen(true);
    }
  };

  const confirmDelete = async () => {
    if (!productToDelete) return;

    // Optimistic UI를 위한 이전 상태 저장
    const previousProducts = [...products];
    const previousSelectedProduct = selectedProduct;

    try {
      // UI에서 즉시 제거
      setProducts((prev) => prev.filter((p) => p.id !== productToDelete.id));

      // 선택된 제품이 삭제된 경우 다른 제품 선택
      if (selectedProduct?.id === productToDelete.id) {
        const remainingProducts = products.filter((p) => p.id !== productToDelete.id);
        setSelectedProduct(remainingProducts.length > 0 ? remainingProducts[0] : null);
      }

      // API 호출
      await deleteInventoryItem(productToDelete.id);

      // 성공 알림
      toast({
        title: "삭제 완료",
        description: "아이템이 인벤토리에서 제거되었습니다.",
        variant: "default",
      });

      setDeleteDialogOpen(false);
      setProductToDelete(null);
    } catch (error: any) {
      // 실패 시 원래 상태로 복원
      setProducts(previousProducts);
      setSelectedProduct(previousSelectedProduct);

      // 에러 알림
      const errorMessage = error.response?.data?.message || "아이템 삭제 중 오류가 발생했습니다.";
      toast({
        title: "삭제 실패",
        description: errorMessage,
        variant: "destructive",
      });
    }
  };

  const handleAddProduct = () => {
    setRegisterModalOpen(true);
  };

  const handleAddPhoto = () => {
    setPhotoModalOpen(true);
  };

  const resetPhotoFlow = useCallback(() => {
    setPhotoStep("camera");
    setCapturedImage(null);
    setRecognizedProduct(null);
    setPhotoError(null);
    setCameraError(null);
    setRegistering(false);
  }, []);

  const handleProductsAdded = async (selectedProducts: Product[]) => {
    try {
      // 현재 인벤토리 목록을 최신 상태로 가져오기
      await fetchInventory();

      // 중복 체크: 현재 인벤토리에 이미 있는 상품 찾기
      const duplicateProducts: Product[] = [];
      const productsToAdd: Product[] = [];

      selectedProducts.forEach((selectedProduct) => {
        // products 배열에서 같은 이름의 상품이 있는지 확인
        const isDuplicate = products.some((existingProduct) => existingProduct.name === selectedProduct.name);

        if (isDuplicate) {
          duplicateProducts.push(selectedProduct);
        } else {
          productsToAdd.push(selectedProduct);
        }
      });

      // 중복된 상품이 있으면 경고 메시지 표시
      if (duplicateProducts.length > 0) {
        const duplicateNames = duplicateProducts.map((p) => `"${p.name}"`).join(", ");

        if (productsToAdd.length > 0) {
          const confirmMessage = `다음 상품은 이미 인벤토리에 존재합니다:\n${duplicateNames}\n\n나머지 ${productsToAdd.length}개 상품을 추가하시겠습니까?`;

          const confirmed = await requestConfirm({
            title: "상품 추가 확인",
            message: confirmMessage,
            type: "warning",
            confirmText: "추가",
            cancelText: "취소",
          });
          if (!confirmed) {
            return; // 사용자가 취소한 경우
          }
        } else {
          showAlert({
            title: "중복 상품",
            message: `선택한 모든 상품이 이미 인벤토리에 존재합니다:\n${duplicateNames}`,
            type: "info",
          });
          return;
        }
      }

      // 중복되지 않은 상품만 추가
      if (productsToAdd.length === 0) {
        return;
      }

      const results = await Promise.allSettled(
        productsToAdd.map((product) =>
          addInventoryItem({
            productId: parseInt(product.id),
          })
            .then(() => ({ product, success: true }))
            .catch((error) => ({ product, success: false, error }))
        )
      );

      // 성공/실패 결과 분류
      const successResults = results.filter((r) => r.status === "fulfilled" && r.value.success).map((r) => (r as PromiseFulfilledResult<any>).value.product);

      const failedResults = results.filter((r) => r.status === "fulfilled" && !r.value.success).map((r) => (r as PromiseFulfilledResult<any>).value);

      // 에러 메시지 생성
      const errorMessages: string[] = [];

      if (failedResults.length > 0) {
        const failedNames = failedResults.map(({ product }) => `"${product.name}"`).join(", ");
        errorMessages.push(`다음 상품 추가 중 오류가 발생했습니다:\n${failedNames}`);
      }

      // 결과 알림
      if (successResults.length > 0 && (duplicateProducts.length > 0 || errorMessages.length > 0)) {
        const messages = [`${successResults.length}개의 상품이 추가되었습니다.`];
        if (duplicateProducts.length > 0) {
          const duplicateNames = duplicateProducts.map((p) => `"${p.name}"`).join(", ");
          messages.push(`${duplicateProducts.length}개의 중복 상품은 제외되었습니다:\n${duplicateNames}`);
        }
        if (errorMessages.length > 0) {
          messages.push(...errorMessages);
        }
        showAlert({
          title: "상품 추가 결과",
          message: messages.join("\n\n"),
          type: errorMessages.length > 0 ? "warning" : "success",
        });
      } else if (successResults.length > 0) {
        showAlert({
          title: "상품 추가 완료",
          message: `${successResults.length}개의 상품이 인벤토리에 추가되었습니다.`,
          type: "success",
        });
      } else if (errorMessages.length > 0) {
        showAlert({
          title: "상품 추가 실패",
          message: errorMessages.join("\n\n"),
          type: "error",
        });
      }

      // API 호출 후 데이터 재조회
      await fetchInventory();
    } catch (error) {
      console.error("상품 추가 중 예상치 못한 오류:", error);
      showAlert({
        title: "상품 추가 실패",
        message: "상품 추가 중 오류가 발생했습니다.",
        type: "error",
      });
    }
  };

  const handleStatusChange = async (productId: string, newStatus: ItemStatus) => {
    const product = products.find((p) => p.id === productId);
    if (!product) return;

    // 이전 상태 저장 (롤백용)
    const previousProducts = [...products];
    const previousSelectedProduct = selectedProduct;

    try {
      // Optimistic UI 업데이트
      const newUiStatus = mapApiStatusToUiStatus(newStatus);
      setProducts((prev) => prev.map((p) => (p.id === productId ? { ...p, status: newUiStatus } : p)));

      if (selectedProduct?.id === productId) {
        setSelectedProduct((prev) => (prev ? { ...prev, status: newUiStatus } : null));
      }

      // API 호출
      await updateInventoryItemStatus(parseInt(productId), newStatus);

      toast({
        title: "상태 변경 완료",
        description: "아이템 상태가 업데이트되었습니다.",
      });
    } catch (error: any) {
      // 실패 시 원래 상태로 복원
      setProducts(previousProducts);
      setSelectedProduct(previousSelectedProduct);

      const errorMessage = getStatusUpdateErrorMessage(error);
      toast({
        title: "상태 변경 실패",
        description: errorMessage,
        variant: "destructive",
      });
    }
  };

  const stopCamera = useCallback(() => {
    if (mediaStreamRef.current) {
      mediaStreamRef.current.getTracks().forEach((track) => track.stop());
      mediaStreamRef.current = null;
    }
    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }
    setCameraLoading(false);
  }, []);

  const startCamera = useCallback(async () => {
    try {
      setCameraError(null);
      setCameraLoading(true);
      if (!navigator.mediaDevices?.getUserMedia) {
        setCameraError("이 브라우저에서는 카메라를 사용할 수 없습니다.");
        return;
      }

      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { ideal: 1280 },
          height: { ideal: 720 },
          facingMode: "environment",
        },
        audio: false,
      });

      if (!videoRef.current) {
        stream.getTracks().forEach((track) => track.stop());
        setCameraError("비디오 요소를 찾을 수 없습니다.");
        return;
      }

      mediaStreamRef.current = stream;
      videoRef.current.srcObject = stream;

      await new Promise<void>((resolve, reject) => {
        const video = videoRef.current;
        if (!video) {
          reject(new Error("비디오 요소가 손실되었습니다."));
          return;
        }

        video.onloadedmetadata = async () => {
          try {
            await video.play();
            resolve();
          } catch (error) {
            reject(error);
          }
        };

        setTimeout(() => reject(new Error("Timeout")), 5000);
      });
    } catch (error) {
      if (error instanceof Error) {
        if (error.name === "NotAllowedError") {
          setCameraError("카메라 접근 권한이 거부되었습니다.");
        } else if (error.name === "NotFoundError") {
          setCameraError("카메라를 찾을 수 없습니다.");
        } else if (error.name === "NotReadableError") {
          setCameraError("다른 앱에서 카메라를 사용 중입니다.");
        } else if (error.message.includes("Timeout")) {
          setCameraError("카메라 로딩 시간이 초과되었습니다.");
        } else {
          setCameraError(`카메라를 시작할 수 없습니다: ${error.message}`);
        }
      } else {
        setCameraError("카메라를 시작할 수 없습니다.");
      }
    } finally {
      setCameraLoading(false);
    }
  }, []);

  useEffect(() => {
    let isActive = true;

    const openCamera = async () => {
      await startCamera();
      if (!isActive) {
        stopCamera();
      }
    };

    if (photoModalOpen && photoStep === "camera") {
      openCamera();
    }

    return () => {
      isActive = false;
      stopCamera();
    };
  }, [photoModalOpen, photoStep, startCamera, stopCamera]);

  useEffect(() => {
    if (!photoModalOpen) {
      resetPhotoFlow();
      stopCamera();
    }
  }, [photoModalOpen, resetPhotoFlow, stopCamera]);

  const handleTakePhoto = () => {
    if (cameraLoading) {
      setPhotoError("카메라를 준비하는 중입니다.");
      return;
    }
    if (!videoRef.current) {
      setPhotoError("카메라가 준비되지 않았습니다.");
      return;
    }

    const video = videoRef.current;
    const canvas = document.createElement("canvas");
    const width = video.videoWidth || 640;
    const height = video.videoHeight || 480;
    if (!width || !height) {
      setPhotoError("카메라 영상이 준비되지 않았습니다.");
      return;
    }
    canvas.width = width;
    canvas.height = height;

    const context = canvas.getContext("2d");
    if (!context) {
      setPhotoError("이미지 캡처에 실패했습니다.");
      return;
    }

    context.drawImage(video, 0, 0, width, height);
    const dataUrl = canvas.toDataURL("image/jpeg", 0.9);
    setCapturedImage(dataUrl);
    setPhotoStep("preview");
    stopCamera();
  };

  const handleRetake = () => {
    setCapturedImage(null);
    setRecognizedProduct(null);
    setPhotoError(null);
    setCameraError(null);
    setPhotoStep("camera");
  };

  const handleConfirmPhoto = async () => {
    if (!capturedImage || photoStep === "loading") return;

    setPhotoStep("loading");
    setPhotoError(null);
    try {
      const response = await recognizeProductByImage(capturedImage);
      if (!response.success || !response.data) {
        const message = response.error?.message || "상품 인식에 실패했습니다.";
        setPhotoError(message);
        setPhotoStep("preview");
        return;
      }
      setRecognizedProduct(response.data);
      setPhotoStep("result");
    } catch (error) {
      setPhotoError("상품 인식 중 오류가 발생했습니다.");
      setPhotoStep("preview");
    }
  };

  const handleRegisterRecognized = async () => {
    if (!recognizedProduct || registering) return;
    setRegistering(true);
    setPhotoError(null);
    try {
      await addInventoryItem({ productId: recognizedProduct.productId });
      await fetchInventory();
      toast({
        title: "등록 완료",
        description: "인식된 상품이 인벤토리에 추가되었습니다.",
      });
      setPhotoModalOpen(false);
    } catch (error: any) {
      const message = error?.response?.data?.message || "상품 등록 중 오류가 발생했습니다.";
      setPhotoError(message);
    } finally {
      setRegistering(false);
    }
  };

  const handleRegisterViaText = () => {
    setPhotoModalOpen(false);
    setRegisterModalOpen(true);
  };

  const isEmptyState = hasFetched && !loading && products.length === 0;
  const fallbackProduct: Product = {
    id: "",
    name: "-",
    brand: "-",
    category: "skin",
    image: "",
    purchaseDate: "",
    expirationDate: "",
    repurchaseCount: 0,
    status: "in-use",
    purchaseLink: "",
    isFavorite: false,
  };
  const detailProduct = selectedProduct ?? fallbackProduct;

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-[1200px] px-6 py-8">
        {/* Main Content */}
        <div className="grid gap-6 lg:grid-cols-3">
          {/* Left Section - Product List */}
          <div className="lg:col-span-2">
            <InventoryFilters
              searchQuery={searchQuery}
              onSearchChange={setSearchQuery}
              selectedCategory={selectedCategory}
              onCategoryChange={setSelectedCategory}
              sortOption={sortOption}
              onSortChange={setSortOption}
              selectedStatus={selectedStatus}
              onStatusChange={setSelectedStatus}
              favoriteOnly={favoriteFilter}
              onFavoriteOnlyChange={setFavoriteFilter}
              onAddPhoto={handleAddPhoto}
              onAddProduct={handleAddProduct}
            />

            <ProductGrid products={filteredProducts} selectedProductId={selectedProduct?.id} onProductSelect={handleProductSelect} />
            {shouldShowPagination && (
              <div className="mt-16 flex flex-wrap items-center justify-center gap-2">
                <Button variant="outline" className="h-9 w-9 p-0" disabled={currentPage === 0} onClick={() => setCurrentPage(currentPage - 1)}>
                  ←
                </Button>
                {pageNumbers.map((page) => {
                  const isActive = page === currentPage;
                  return (
                    <Button
                      key={page}
                      variant="outline"
                      className={`h-9 w-9 p-0 ${isActive ? "border-primary-500 bg-primary-500 text-dark-bg hover:bg-primary-400" : "text-muted-foreground"}`}
                      onClick={() => setCurrentPage(page)}
                    >
                      {page + 1}
                    </Button>
                  );
                })}
                <Button variant="outline" className="h-9 w-9 p-0" disabled={currentPage >= totalPages - 1} onClick={() => setCurrentPage(currentPage + 1)}>
                  →
                </Button>
              </div>
            )}
          </div>

          {/* Right Section - Product Detail */}
          <div className="lg:col-span-1">
            <ProductDetail product={detailProduct} onToggleFavorite={handleToggleFavorite} onDelete={handleDelete} onStatusChange={handleStatusChange} loading={detailLoading} isEmpty={isEmptyState} />
          </div>
        </div>
      </div>

      {/* Inventory Register Modal */}
      <InventoryRegisterModal open={registerModalOpen} onOpenChange={setRegisterModalOpen} onConfirm={handleProductsAdded} />

      {/* Photo Registration Modal */}
      <Dialog
        open={photoModalOpen}
        onOpenChange={(open) => {
          if (photoStep === "loading") return;
          setPhotoModalOpen(open);
        }}
      >
        <DialogContent className="max-w-xl">
          <DialogHeader>
            <DialogTitle>사진 등록</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            {photoStep === "camera" && (
              <>
                {cameraError ? (
                  <p className="text-sm text-muted-foreground">{cameraError}</p>
                ) : (
                  <div className="overflow-hidden rounded-lg border border-border bg-black">
                    <video ref={videoRef} autoPlay playsInline muted className="h-[360px] w-full object-cover" />
                  </div>
                )}
                <Button onClick={handleTakePhoto} disabled={!!cameraError || cameraLoading} className="w-full bg-primary-500 text-dark-bg hover:bg-primary-400">
                  {cameraLoading ? "카메라 준비 중..." : "사진 촬영"}
                </Button>
              </>
            )}

            {photoStep === "preview" && (
              <>
                {capturedImage && (
                  <div className="overflow-hidden rounded-lg border border-border bg-black">
                    <img src={capturedImage} alt="촬영된 사진" className="h-[360px] w-full object-cover" />
                  </div>
                )}
                {photoError && <p className="text-sm text-destructive">{photoError}</p>}
                <div className="flex gap-3">
                  <Button variant="outline" onClick={handleRetake} className="flex-1">
                    다시 촬영
                  </Button>
                  <Button onClick={handleConfirmPhoto} className="flex-1 bg-primary-500 text-dark-bg hover:bg-primary-400">
                    확인
                  </Button>
                </div>
              </>
            )}

            {photoStep === "loading" && (
              <div className="flex flex-col items-center gap-4 py-6">
                <div className="h-10 w-10 animate-spin rounded-full border-2 border-primary-500 border-t-transparent" />
                <p className="text-sm text-muted-foreground">상품을 인식하고 있습니다...</p>
              </div>
            )}

            {photoStep === "result" && recognizedProduct && (
              <>
                <div className="flex items-center gap-4 rounded-lg border border-border p-4">
                  <div className="h-20 w-20 overflow-hidden rounded-md border border-border bg-muted">
                    <img src={recognizedProduct.imageUrl} alt={recognizedProduct.name} className="h-full w-full object-cover" />
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm font-semibold text-foreground">{recognizedProduct.name}</p>
                    <p className="text-xs text-muted-foreground">{recognizedProduct.brandName}</p>
                    <p className="text-xs text-muted-foreground">{recognizedProduct.categoryMedium}</p>
                  </div>
                </div>
                {photoError && <p className="text-sm text-destructive">{photoError}</p>}
                <div className="flex flex-col gap-2">
                  <div className="flex gap-3">
                    <Button variant="outline" onClick={handleRetake} className="flex-1" disabled={registering}>
                      다시 촬영
                    </Button>
                    <Button onClick={handleRegisterRecognized} className="flex-1 bg-primary-500 text-dark-bg hover:bg-primary-400" disabled={registering}>
                      등록
                    </Button>
                  </div>
                  <Button variant="outline" onClick={handleRegisterViaText} disabled={registering}>
                    텍스트로 등록
                  </Button>
                </div>
              </>
            )}
          </div>
        </DialogContent>
      </Dialog>

      <AlertModal
        open={alertState.open}
        onOpenChange={(open) => setAlertState((prev) => ({ ...prev, open }))}
        title={alertState.title}
        message={alertState.message}
        type={alertState.type}
        confirmText={alertState.confirmText}
      />

      <ConfirmModal
        open={confirmState.open}
        onOpenChange={(open) => {
          if (!open) handleConfirmResult(false);
        }}
        onConfirm={() => handleConfirmResult(true)}
        title={confirmState.title}
        message={confirmState.message}
        type={confirmState.type}
        confirmText={confirmState.confirmText}
        cancelText={confirmState.cancelText}
      />

      <ConfirmModal
        open={deleteDialogOpen}
        onOpenChange={(open) => {
          if (!open) {
            setDeleteDialogOpen(false);
            setProductToDelete(null);
          }
        }}
        onConfirm={confirmDelete}
        title="아이템 삭제"
        message={
          <>
            이 아이템을 인벤토리에서 제거하시겠습니까?
            {productToDelete && <span className="mt-2 block font-medium text-foreground">{productToDelete.name}</span>}
          </>
        }
        type="error"
        confirmText="삭제"
        cancelText="취소"
      />
    </div>
  );
}
